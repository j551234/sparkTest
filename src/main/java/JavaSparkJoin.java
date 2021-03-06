import org.apache.spark.sql.*;

import javax.xml.crypto.Data;

public class JavaSparkJoin {
    public static void main(String[] args) {
        SparkSession spark = SparkSession
                .builder()
                .config("spark.sql.warehouse.dir", "/user/hive/warehouse/")
                .config("spark.sql.legacy.allowCreatingManagedTableUsingNonemptyLocation", "true")
                .appName("JavaSpark")
                .getOrCreate();


        Encoder<TableModel> personEncoder = Encoders.bean(TableModel.class);
        Dataset<TableModel> newTable1 =
                spark.sql("select * from test.newtable1  ").as(personEncoder);
        newTable1.show();
        Dataset<TableModel> table1 =
                spark.sql("select * from test.table1  ").as(personEncoder);
        table1.show();
        // example for join
        Dataset fullTable = table1.join(newTable1, newTable1.col("t3").equalTo(table1.col("t3")), "left");
        fullTable.show();

        // example for join select
        Dataset<TableModel> joinRow = table1.join(newTable1, (
                newTable1.col("t3").equalTo(table1.col("t3"))), "left")
                .select(newTable1.col("t1").alias("t1"),
                        table1.col("t2").alias("t2"),
                        newTable1.col("t3").alias("t3")).as(personEncoder);
        joinRow.show();

        // example for arg

        Dataset<Row> showTable =
                spark.sql("select * from test.newtable1  ");

        Dataset<Row> aggTable =showTable.groupBy("t1").agg(
                functions.count("t1").as("count"),
                functions.sum("t3").as("maxt3")
        );
        aggTable.show();
        spark.stop();
    }
}
