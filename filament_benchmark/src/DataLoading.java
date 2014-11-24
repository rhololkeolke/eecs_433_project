import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.fgraph.Node;
import org.fgraph.base.DefaultGraph;
import org.fgraph.tstore.sql.SqlStoreFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by rhol on 11/24/14.
 */
public class DataLoading {

    public static void main(String[] args) {
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

        Map<String, Node> nodes = new HashMap<String, Node>();

        try {
            Reader in = new FileReader(args[3]);
            CSVFormat dump_format = CSVFormat.newFormat(' ');
            Iterable<CSVRecord> records = dump_format.parse(in);

            int i=0;
            for (CSVRecord record : records) {
                if(i % 1000 == 0) {

                    System.out.println("On record " + i);
                    conn.commit();
                }

                String node1_name = record.get(0);
                String edge_name = record.get(1);
                String node2_name = record.get(2);

                Node n1 = nodes.get(node1_name);
                if(n1 == null) {
                    n1 = graph.newNode();
                    n1.add(node1_name, "");
                    nodes.put(node1_name, n1);
                }

                Node n2 = nodes.get(node2_name);
                if(n2 == null) {
                    n2 = graph.newNode();
                    n2.add(node2_name, "");
                    nodes.put(node2_name, n2);
                }

                graph.addEdge(n1, n2, edge_name);

                i++;
            }
        } catch (IOException e) {
            System.err.println("Failed to parse CSV" + e.getMessage());
            try {
                conn.close();
            } catch (SQLException e1) {
                System.err.println("Failed to close connection. " + e.getMessage());
            }
            System.exit(1);
        } catch (SQLException e) {
            System.err.println("Failed to commit nodes. " + e.getMessage());
            try {
                conn.close();
            } catch (SQLException e1) {
                System.err.println("Failed to close connection. " + e.getMessage());
            }
            System.exit(1);
        }

        try {
            conn.commit();
        } catch (SQLException e) {
            System.err.println("Failed to commit nodes. " + e.getMessage());
            try {
                conn.close();
            } catch (SQLException e1) {
                System.err.println("Failed to close connection. " + e.getMessage());
            }
            System.exit(1);
        }

        try {
            conn.close();
        } catch(SQLException e) {
            System.err.println("Failed to close connection. " + e.getMessage());
            System.exit(1);
        }
    }
}
