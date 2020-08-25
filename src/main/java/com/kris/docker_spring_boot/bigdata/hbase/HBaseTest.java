package com.kris.docker_spring_boot.bigdata.hbase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;

import org.apache.commons.lang.time.StopWatch;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.client.coprocessor.AggregationClient;
import org.apache.hadoop.hbase.client.coprocessor.LongColumnInterpreter;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.filter.SubstringComparator;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HBaseTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Configuration config;

    private static Connection connection;

    private static Admin admin;

    public void setConfig() {

        // hbase的两种连接方式：1）读取配置文件 只需要配置zookeeper
        // config = HBaseConfiguration.create();
        // try {
        // connection = ConnectionFactory.createConnection(config);
        // } catch (IOException ignore) {}
        // 2）通过代码配置
        Configuration configuration = new Configuration();
        configuration.set("hbase.zookeeper.quorum", "192.168.243.128:2181,192.168.243.129:2181,192.168.243.130:2181");
        try {
            connection = ConnectionFactory.createConnection();
        } catch (IOException ignore) {}
    }

    public static void main(String[] args) throws IOException {
        // 1.连接HBase
        // 1.1 HBaseConfiguration.create();
        Configuration config = HBaseConfiguration.create();
        // 1.2 创建一个连接
        Connection connection = ConnectionFactory.createConnection(config);
        // 1.3 从连接中获得一个Admin对象
        Admin admin = connection.getAdmin();
        // 2.创建表
        // 2.1 判断表是否存在
        TableName tableName = TableName.valueOf("user2");
        if (!admin.tableExists(tableName)) {
            // 2.2 如果表不存在就创建一个表
            System.out.println(">>>>>>>>>>>>>>>>创建表");
            HTableDescriptor hTableDescriptor = new HTableDescriptor(tableName);
            hTableDescriptor.addFamily(new HColumnDescriptor("base_info2"));
            admin.createTable(hTableDescriptor);
        }
    }

    @Before
    public void initConnection() {
        try {
            config = HBaseConfiguration.create();
            connection = ConnectionFactory.createConnection(config);
            admin = connection.getAdmin();
        } catch (IOException e) {
            System.out.println("连接数据库失败");
        }
    }

    @Test
    public void tableInfo() throws IOException {
        // 1.定义表的名称
        TableName tableName = TableName.valueOf("user");
        // 2.获取表
        Table table = connection.getTable(tableName);
        // 3.获取表的描述信息
        HTableDescriptor tableDescriptor = table.getTableDescriptor();
        // 4.获取表的列簇信息
        HColumnDescriptor[] columnFamilies = tableDescriptor.getColumnFamilies();
        for (HColumnDescriptor columnFamily : columnFamilies) {
            // 5.获取表的columFamily的字节数组
            byte[] name = columnFamily.getName();
            // 6.使用hbase自带的bytes工具类转成string
            String value = Bytes.toString(name);
            // 7.打印
            System.out.println(">>>>>>>>>>>>>>>" + value);
        }
    }

    /**
     * 创建表
     *
     * @param tableName
     *            表名
     * @param columnFamily
     *            列族（数组）
     */
    public void createTable(String tableName, String[] columnFamily) throws IOException {
        TableName name = TableName.valueOf(tableName);
        // 如果存在则删除
        if (admin.tableExists(name)) {
            admin.disableTable(name);
            admin.deleteTable(name);
            logger.error("create htable error! this table {} already exists!", name);
        } else {
            HTableDescriptor desc = new HTableDescriptor(name);
            for (String cf : columnFamily) {
                desc.addFamily(new HColumnDescriptor(cf));
            }
            admin.createTable(desc);
        }
    }

    /**
     * 插入记录（单行单列族-多列多值）
     *
     * @param tableName
     *            表名
     * @param row
     *            行名
     * @param columnFamilys
     *            列族名
     * @param columns
     *            列名（数组）
     * @param values
     *            值（数组）（且需要和列一一对应）
     */
    public void insertRecords(String tableName, String row, String columnFamilys, String[] columns, String[] values)
            throws IOException {
        TableName name = TableName.valueOf(tableName);
        Table table = connection.getTable(name);
        Put put = new Put(Bytes.toBytes(row));
        for (int i = 0; i < columns.length; i++) {
            put.addColumn(Bytes.toBytes(columnFamilys), Bytes.toBytes(columns[i]), Bytes.toBytes(values[i]));
            table.put(put);
        }
    }

    /**
     * 插入记录（单行单列族-单列单值）
     *
     * @param tableName
     *            表名
     * @param row
     *            行名
     * @param columnFamily
     *            列族名
     * @param column
     *            列名
     * @param value
     *            值
     */
    public void insertOneRecord(String tableName, String row, String columnFamily, String column, String value)
            throws IOException {
        TableName name = TableName.valueOf(tableName);
        Table table = connection.getTable(name);
        Put put = new Put(Bytes.toBytes(row));
        put.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(column), Bytes.toBytes(value));
        table.put(put);
    }

    /**
     * 删除一行记录
     *
     * @param tablename
     *            表名
     * @param rowkey
     *            行名
     */
    public void deleteRow(String tablename, String rowkey) throws IOException {
        TableName name = TableName.valueOf(tablename);
        Table table = connection.getTable(name);
        Delete d = new Delete(rowkey.getBytes());
        table.delete(d);
    }

    /**
     * 删除单行单列族记录
     * 
     * @param tablename
     *            表名
     * @param rowkey
     *            行名
     * @param columnFamily
     *            列族名
     */
    public void deleteColumnFamily(String tablename, String rowkey, String columnFamily) throws IOException {
        TableName name = TableName.valueOf(tablename);
        Table table = connection.getTable(name);
        Delete d = new Delete(rowkey.getBytes()).deleteFamily(Bytes.toBytes(columnFamily));
        table.delete(d);
    }

    /**
     * 删除单行单列族单列记录
     *
     * @param tablename
     *            表名
     * @param rowkey
     *            行名
     * @param columnFamily
     *            列族名
     * @param column
     *            列名
     */
    public void deleteColumn(String tablename, String rowkey, String columnFamily, String column) throws IOException {
        TableName name = TableName.valueOf(tablename);
        Table table = connection.getTable(name);
        Delete d = new Delete(rowkey.getBytes()).deleteColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(column));
        table.delete(d);
    }

    /**
     * 查找一行记录
     *
     * @param tablename
     *            表名
     * @param rowKey
     *            行名
     */
    // public static String selectRow(String tablename, String rowKey) throws
    // IOException {
    @Test
    public void selectRow() throws IOException {
        String record = "";
        TableName name = TableName.valueOf("user");
        Table table = connection.getTable(name);
        Get g = new Get("rowkey_10".getBytes());
        Result rs = table.get(g);
        NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> map = rs.getMap();
        for (Cell cell : rs.rawCells()) {
            StringBuffer stringBuffer = new StringBuffer().append(Bytes.toString(cell.getRow())).append("\t")
                    .append(Bytes.toString(cell.getFamily())).append("\t").append(Bytes.toString(cell.getQualifier()))
                    .append("\t").append(Bytes.toString(cell.getValue())).append("\n");
            String str = stringBuffer.toString();
            record += str;
        }
        System.out.println(">>>>>>>>>>>>>>" + record);
    }

    /**
     * 查找单行单列族单列记录
     *
     * @param tablename
     *            表名
     * @param rowKey
     *            行名
     * @param columnFamily
     *            列族名
     * @param column
     *            列名
     * @return
     */
    public static String selectValue(String tablename, String rowKey, String columnFamily, String column)
            throws IOException {
        TableName name = TableName.valueOf(tablename);
        Table table = connection.getTable(name);
        Get g = new Get(rowKey.getBytes());
        g.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(column));
        Result rs = table.get(g);
        return Bytes.toString(rs.value());
    }

    /**
     * 查询表中所有行（Scan方式）
     *
     * @param tablename
     * @return
     */
    public String scanAllRecord(String tablename) throws IOException {
        String record = "";
        TableName name = TableName.valueOf(tablename);
        Table table = connection.getTable(name);
        Scan scan = new Scan();
        ResultScanner scanner = table.getScanner(scan);
        try {
            for (Result result : scanner) {
                for (Cell cell : result.rawCells()) {
                    StringBuffer stringBuffer = new StringBuffer().append(Bytes.toString(cell.getRow())).append("\t")
                            .append(Bytes.toString(cell.getFamily())).append("\t")
                            .append(Bytes.toString(cell.getQualifier())).append("\t")
                            .append(Bytes.toString(cell.getValue())).append("\n");
                    String str = stringBuffer.toString();
                    record += str;
                }
            }
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }

        return record;
    }

    /**
     * 根据rowkey关键字查询报告记录
     *
     * @param tablename
     * @param rowKeyword
     * @return
     */
    public List scanReportDataByRowKeyword(String tablename, String rowKeyword) throws IOException {
        List list = new ArrayList<>();

        Table table = connection.getTable(TableName.valueOf(tablename));
        Scan scan = new Scan();

        // 添加行键过滤器，根据关键字匹配
        RowFilter rowFilter = new RowFilter(CompareFilter.CompareOp.EQUAL, new SubstringComparator(rowKeyword));
        scan.setFilter(rowFilter);

        ResultScanner scanner = table.getScanner(scan);
        try {
            for (Result result : scanner) {
                // TODO 此处根据业务来自定义实现
                list.add(null);
            }
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }

        return list;
    }

    /**
     * 根据rowkey关键字和时间戳范围查询报告记录
     *
     * @param tablename
     * @param rowKeyword
     * @return
     */
    public List scanReportDataByRowKeywordTimestamp(String tablename, String rowKeyword, Long minStamp, Long maxStamp)
            throws IOException {
        List list = new ArrayList<>();

        Table table = connection.getTable(TableName.valueOf(tablename));
        Scan scan = new Scan();
        // 添加scan的时间范围
        scan.setTimeRange(minStamp, maxStamp);

        RowFilter rowFilter = new RowFilter(CompareFilter.CompareOp.EQUAL, new SubstringComparator(rowKeyword));
        scan.setFilter(rowFilter);

        ResultScanner scanner = table.getScanner(scan);
        try {
            for (Result result : scanner) {
                // TODO 此处根据业务来自定义实现
                list.add(null);
            }
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }

        return list;
    }

    /**
     * 删除表操作
     *
     * @param tablename
     */
    public void deleteTable(String tablename) throws IOException {
        TableName name = TableName.valueOf(tablename);
        if (admin.tableExists(name)) {
            admin.disableTable(name);
            admin.deleteTable(name);
        }
    }

    /**
     * 利用协处理器进行全表count统计
     *
     * @param tablename
     */
    public Long countRowsWithCoprocessor(String tablename) throws Throwable {
        TableName name = TableName.valueOf(tablename);
        HTableDescriptor descriptor = admin.getTableDescriptor(name);

        String coprocessorClass = "org.apache.hadoop.hbase.coprocessor.AggregateImplementation";
        if (!descriptor.hasCoprocessor(coprocessorClass)) {
            admin.disableTable(name);
            descriptor.addCoprocessor(coprocessorClass);
            admin.modifyTable(name, descriptor);
            admin.enableTable(name);
        }

        // 计时
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Scan scan = new Scan();
        AggregationClient aggregationClient = new AggregationClient(config);

        Long count = aggregationClient.rowCount(name, new LongColumnInterpreter(), scan);

        stopWatch.stop();
        System.out.println("RowCount：" + count + "，全表count统计耗时：" + stopWatch.getTime());

        return count;
    }
}
