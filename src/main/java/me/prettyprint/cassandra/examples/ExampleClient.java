package me.prettyprint.cassandra.examples;

import static me.prettyprint.cassandra.utils.StringUtils.bytes;
import static me.prettyprint.cassandra.utils.StringUtils.string;
import me.prettyprint.cassandra.model.HectorException;
import me.prettyprint.cassandra.service.CassandraClient;
import me.prettyprint.cassandra.service.CassandraClientPool;
import me.prettyprint.cassandra.service.CassandraClientPoolFactory;
import me.prettyprint.cassandra.service.Keyspace;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnPath;

/**
 * Example client that uses the cassandra hector client.
 *
 * @author Ran Tavory (rantav@gmail.com)
 *
 */
public class ExampleClient {

  public static void main(String[] args) throws HectorException {
    CassandraClientPool pool = CassandraClientPoolFactory.INSTANCE.get();
    CassandraClient client = pool.borrowClient("tush", 9160);
    // A load balanced version would look like this:
    // CassandraClient client = pool.borrowClient(new String[] {"cas1:9160", "cas2:9160", "cas3:9160"});

    Keyspace keyspace = null;
    try {
      keyspace = client.getKeyspace("Keyspace1");
      ColumnPath columnPath = new ColumnPath("Standard1");
      columnPath.setColumn(bytes("column-name"));

      // insert
      keyspace.insert("key", columnPath, bytes("value"));

      // read
      Column col = keyspace.getColumn("key", columnPath);

      System.out.println("Read from cassandra: " + string(col.getValue()));

    } finally {
      // This line makes sure that even if the client had failures and recovered, a correct
      // releaseClient is called, on the up to date client.
      if (keyspace != null) {
        client = keyspace.getClient();
        // return client to pool. do it in a finally block to make sure it's executed
        pool.releaseClient(client);
      }
    }
  }
}
