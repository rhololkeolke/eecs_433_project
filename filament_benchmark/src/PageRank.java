import org.fgraph.Direction;
import org.fgraph.Edge;
import org.fgraph.Goid;
import org.fgraph.Node;
import org.fgraph.base.DefaultGraph;
import org.fgraph.tstore.sql.SqlStoreFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rhol on 11/29/14.
 */
public class PageRank {

    public static void main(String[] args)
    {
        String jdbcUrl = "jdbc:postgresql://" + args[0] + ":" + args[1] + "/" + args[2];
        String user = "filament";
        String password = "filament";

        Connection conn = null;
        try {
            conn = DriverManager.getConnection(jdbcUrl, user, password);
        } catch (SQLException e) {
            System.err.println("Failed to open connection to Postgres database. " + e.getMessage());
            System.exit(1);
        }

        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            System.err.println("Failed to set database connection to manual commit");
            try {
                conn.close();
            } catch (SQLException e1) {
                System.err.println("Failed to close connection. " + e.getMessage());
            }
            System.exit(1);
        }

        DefaultGraph graph = DefaultGraph.create(new SqlStoreFactory(conn));

        // get the out degree of each node in the graph
        Map<Goid, Integer> out_degree = new HashMap<Goid, Integer>();
        for(Node n : graph.nodes())
        {
            Collection<Edge> out_edges = n.edges(null, Direction.OUT);
            out_degree.put(n.getId(), out_edges.size());
        }

        double damping_factor = .85;

        // initialize the page rank guesses
        Map<Goid, Double> page_rank = new HashMap<Goid, Double>();
        for(Node n : graph.nodes())
        {
            page_rank.put(n.getId(), 1-damping_factor);
        }
        double difference = Double.MAX_VALUE;

        int iteration = 0;
        while(difference > 1.0e-3) {
            difference = 0;

            for (Node n : graph.nodes()) {
                double this_page_rank = 1 - damping_factor;
                double incoming_summation = 0;
                Collection<Edge> in_edges = n.edges(null, Direction.IN);
                for(Edge e : in_edges)
                {
                    Goid head_id = e.getTail().getId();
                    incoming_summation += page_rank.get(head_id)/out_degree.get(head_id);
                }

                this_page_rank += damping_factor*incoming_summation;

                difference += Math.abs(this_page_rank - page_rank.get(n.getId()));

                page_rank.put(n.getId(), this_page_rank);
            }

            System.out.println("Difference is: " + difference + " on iteration " + iteration);
        }

        System.out.println("Page ranks are");
        for(Node n : graph.nodes())
        {
            System.out.println("Node " + n.toString() + " has page rank " + page_rank.get(n.getId()));
        }

        try {
            conn.close();
        } catch(SQLException e) {
            System.err.println("Failed to close connection. " + e.getMessage());
            System.exit(1);
        }
    }
}
