import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
 
import java.io.File;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import java.io.*;
import java.lang.Integer;
import java.util.LinkedList;
import java.util.HashMap;
import java.lang.management.*;


public class App
{
    private static final String DB_PATH = "target/neo4j-hello-db";

    public String greeting;

    // START SNIPPET: vars
    GraphDatabaseService graphDb;
    Node firstNode;
    Node secondNode;
    Relationship relationship;
    
    Node [] nodes = new Node[100000000];
    // END SNIPPET: vars

    // START SNIPPET: createReltype
    private static enum RelTypes implements RelationshipType
    {
        EDGE
    }
    // END SNIPPET: createReltype

    public static void main( final String[] args )
    {
        App hello = new App();
        hello.createDb();
        hello.removeData();
        hello.shutDown();
       
    }

    void createDb()
    {
    	String csvFile = "/Users/yigit/Desktop/dump.nt";
    	BufferedReader br = null;
    	String line = "";
    	String cvsSplitBy = " ";
    	int v=0;
    
     
        deleteFileOrDirectory( new File( DB_PATH ) );
        // START SNIPPET: startDb
        graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
        registerShutdownHook( graphDb );
        // END SNIPPET: startDb

        // START SNIPPET: transaction
        try ( Transaction tx = graphDb.beginTx() )
        {
        	try {
        	     
        		br = new BufferedReader(new FileReader(csvFile));
        		while ((line = br.readLine()) != null) {
         
        		        // use space as seperator
        			String[] linkedmdb= line.split(cvsSplitBy);
        			
					nodes[v] = graphDb.createNode();
					nodes[v+1] = graphDb.createNode();
					nodes[v].setProperty("id", linkedmdb[0]);
					nodes[v+1].setProperty("id", linkedmdb[2]);
					Relationship rel = nodes[v].createRelationshipTo(nodes[v+1], RelTypes.EDGE);
					rel.setProperty("link", linkedmdb[1]);
					v=v+2;
					
        		}
        		tx.success();
        		
        	} catch (FileNotFoundException e) {
        		e.printStackTrace();
        	} catch (IOException e) {
        		e.printStackTrace();
        	} finally {
        		if (br != null) {
        			try {
        				br.close();
        			} catch (IOException e) {
        				e.printStackTrace();
        			}
        		}
        	}
       
        }
         //END SNIPPET: transaction 
    }

    void removeData()
    {
        try ( Transaction tx = graphDb.beginTx() )
        {
            // START SNIPPET: removingData
            // let's remove the data
            firstNode.getSingleRelationship( RelTypes.EDGE, Direction.OUTGOING ).delete();
            firstNode.delete();
            secondNode.delete();
            // END SNIPPET: removingData

            tx.success();
        }
    }

    void shutDown()
    {
        System.out.println();
        System.out.println( "Shutting down database ..." );
        // START SNIPPET: shutdownServer
        graphDb.shutdown();
        // END SNIPPET: shutdownServer
    }

    // START SNIPPET: shutdownHook
    private static void registerShutdownHook( final GraphDatabaseService graphDb )
    {
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
        // running application).
        Runtime.getRuntime().addShutdownHook( new Thread()
        {
            @Override
            public void run()
            {
                graphDb.shutdown();
            }
        } );
    }
    // END SNIPPET: shutdownHook

    private static void deleteFileOrDirectory( File file )
    {
        if ( file.exists() )
        {
            if ( file.isDirectory() )
            {
                for ( File child : file.listFiles() )
                {
                    deleteFileOrDirectory( child );
                }
            }
            file.delete();
        }
    }
}