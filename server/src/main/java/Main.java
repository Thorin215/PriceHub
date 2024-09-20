import utils.ConnectConfig;
import utils.DatabaseConnector;
import utils.MysqlInitializer;
//import com.example.Utils;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.net.URI;
import java.util.Map;

import utils.*;
import java.util.Map;
import java.time.LocalDate;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Date;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.json.*;

import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;


import queries.ApiResult;
import queries.BookQueryConditions;
import queries.BookQueryResults;
import queries.BorrowHistories;
import queries.CardList;
import queries.SortOrder;

import entities.Book;
import entities.Borrow;
import entities.Card;


import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.io.IOException;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
//import com.sun.net.httpserver.HttpServer;
//import com.sun.net.httpserver.HttpExchange;
//import com.sun.net.httpserver.HttpHandler;




public class Main {
    private static final Logger log = Logger.getLogger(Main.class.getName());

    static LibraryManagementSystem library;

    //throws IOException
    public static void main(String[] args) throws IOException {
        // 创建HTTP服务器，监听指定端口
        // 这里是8000，建议不要80端口，容易和其他的撞
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
// 添加/card路径的处理程序
        server.createContext("/card", new CardHandler());
// 添加/book路径的处理程序
        server.createContext("/book", new BookHandler());
//添加/borrow路径的处理程序
        server.createContext("/borrow", new BorrowHandler());
// 启动服务器
        server.start();
        // 标识一下，这样才知道我的后端启动了（确信
        System.out.println("Server is listening on port 8000:book");
        try {
            // parse connection config from "resources/application.yaml"
            ConnectConfig conf = new ConnectConfig();
            log.info("Success to parse connect config. " + conf.toString());
            // connect to database
            DatabaseConnector connector = new DatabaseConnector(conf);
            boolean connStatus = connector.connect();
            if (!connStatus) {
                log.severe("Failed to connect database.");
                System.exit(1);
            }
            /* do somethings */
            System.out.println("这是图书管理系统");
            System.out.println("输入1-10中的一个数字来启动对应的功能, 输入-1结束交互");
            System.out.println("1.添加图书");
            System.out.println("2.修改图书余量");
            System.out.println("3.修改图书信息");
            System.out.println("4.批量入库图书");
            System.out.println("5.注册借书证");
            System.out.println("6.查询借书证");
            System.out.println("7.借书");
            System.out.println("8.还书");
            System.out.println("9.查询借书记录");
            System.out.println("10.查询图书信息");
            library = new LibraryManagementSystemImpl(connector);
//            library = new LibraryManagementSystemImpl(connector);
            //library.resetDatabase();
            String command;
            Scanner scanner = new Scanner(System.in);
            command = scanner.nextLine();
            int com = Integer.parseInt(command);

            while (com != -1) {
                if (com == 1) {//存入书籍
                    System.out.println("请输入<类别，书名，出版社，年份，作者，价格，初始库存>(系统将自动分配书号), 属性之间以逗号分隔");
                    command = scanner.nextLine();
                    String[] args1 = command.split(",");
                    //System.out.println(args1[7]);
                    entities.Book book = new Book(args1[0], args1[1], args1[2], Integer.parseInt(args1[3]), args1[4], Double.parseDouble(args1[5]), Integer.parseInt(args1[6]));
                    ApiResult apiResult = library.storeBook(book);
                    if (!apiResult.ok) System.out.println("添加失败!" + apiResult.message);
                    else
                        System.out.println("《" + args1[1] + "》" + "添加成功!  BookID = " + Objects.toString(apiResult.payload));
                } else if (com == 2) {//修改书籍库存
                    System.out.println("请输入<书号，增加的库存>, 属性之间以逗号分隔");
                    command = scanner.nextLine();
                    String[] args1 = command.split(",");
                    ApiResult apiResult = library.incBookStock(Integer.parseInt(args1[0]), Integer.parseInt(args1[1]));
                    if (!apiResult.ok) System.out.println("修改失败!" + apiResult.message);
                    else System.out.println("《" + args1[0] + "》" + "修改成功!");
                } else if (com == 3) {//修改图书信息
                    System.out.println("请输入<书号. 类别，书名，出版社，年份，作者，价格>, 属性之间以逗号分隔");
                    command = scanner.nextLine();
                    String[] args1 = command.split(",");
                    entities.Book book = new Book();
                    if (!Objects.equals(args1[1], " ")) book.setCategory(args1[1]);
                    if (!Objects.equals(args1[2], " ")) book.setTitle(args1[2]);
                    if (!Objects.equals(args1[3], " ")) book.setPress(args1[3]);
                    if (!Objects.equals(args1[4], " ")) book.setPublishYear(Integer.parseInt(args1[4]));
                    if (!Objects.equals(args1[5], " ")) book.setAuthor(args1[5]);
                    if (!Objects.equals(args1[6], " ")) book.setPrice(Double.parseDouble(args1[6]));

                    book.setBookId(Integer.parseInt(args1[0]));
                    ApiResult apiResult = library.modifyBookInfo(book);
                    if (!apiResult.ok) System.out.println("修改失败!");
                    else System.out.println("《" + args1[2] + "》" + "修改成功!");
                } else if (com == 4) {//批量入库
                    System.out.println("请输入批量入库的文件路径名：");
                    command = scanner.nextLine();
                    String filename = command;
                    List<Book> books = new ArrayList<Book>();
                    try (Scanner sc = new Scanner(new File(filename))) {
                        while (sc.hasNextLine()) {
                            String[] args1 = sc.nextLine().split(",");
                            entities.Book book = new Book(args1[1], args1[2], args1[3], Integer.parseInt(args1[4]), args1[5], Double.parseDouble(args1[6]), Integer.parseInt(args1[7]));
                            book.setBookId(Integer.parseInt(args1[0]));
                            books.add(book);
                        }
                    }
                    ApiResult apiResult = library.storeBook(books);
                    if (!apiResult.ok) System.out.println("批量入库失败!" + apiResult.message);
                    else System.out.println("批量入库成功!");
                } else if (com == 5) {//注册借书证
                    System.out.println("请输入你想要注册的借书证的姓名，单位以及身份，身份只能为T(Teacher) 或者 S(Student)");

                    command = scanner.nextLine();
                    String[] args1 = command.split(",");
                    entities.Card card = new Card();
                    card.setName(args1[0]);
                    card.setDepartment(args1[1]);
                    card.setType(Card.CardType.values(args1[2]));
                    ApiResult apiResult = library.registerCard(card);

                    String N;
                    if (args1[2].equals("T")) N = "老师";
                    else N = "学生";
                    String number = ((Integer) apiResult.payload).toString();
                    if (!apiResult.ok) System.out.println("注册失败!" + apiResult.message);
                    else System.out.println(N + args1[0] + "注册成功!" + number);
                } else if (com == 6) {//查询借书证
                    List<Card> cards = ((CardList) library.showCards().payload).getCards();
                    for (Card card : cards) {
                        System.out.println("姓名：" + card.getName() + " 单位：" + card.getDepartment() + " 身份：" + card.getType() + " 编号：" + card.getCardId());
                    }
                } else if (com == 7) {//借书
                    System.out.println("欢迎借书！请输入<借书证号，书号>");
                    command = scanner.nextLine();
                    String[] args1 = command.split(",");
                    Borrow borrow = new Borrow(Integer.parseInt(args1[1]), Integer.parseInt(args1[0]));
                    LocalDate date = LocalDate.now();
                    borrow.resetBorrowTime();
                    //borrow.setBorrowTime(date.getYear()*10000 + date.getMonthValue()*100 + date.getDayOfMonth());
                    ApiResult apiResult = library.borrowBook(borrow);
                    if (!apiResult.ok) System.out.println("借书失败!" + apiResult.message);
                    else {
                        System.out.println("借书成功!");
                        System.out.println("借书证号：" + args1[0] + " 书号：" + args1[1] + " 借书日期：" + Integer.toString(date.getYear()) + "年" + Integer.toString(date.getMonthValue()) + "月" + Integer.toString(date.getDayOfMonth()) + "日");
                        //System.out.println("借书证号：" + args1[0] + " 书号：" + args1[1] + " 借书日期：" );
                    }
                    ;
                } else if (com == 8) {//还书
                    System.out.println("欢迎还书！请输入<借书证号. 书号>");
                    command = scanner.nextLine();
                    String[] args1 = command.split(",");
                    Borrow ret = new Borrow(Integer.parseInt(args1[1]), Integer.parseInt(args1[0]));
                    LocalDate date = LocalDate.now();
                    ret.resetReturnTime();
                    //ret.setReturnTime(date.getYear()*10000 + date.getMonthValue()*100 + date.getDayOfMonth());
                    //ret.setBorrowTime(Integer.parseInt(args1[2]));
                    ApiResult apiResult = library.returnBook(ret);
                    if (!apiResult.ok) System.out.println("还书失败!" + apiResult.message);
                    else {
                        System.out.println("还书成功!");
                        System.out.println("借书证号：" + args1[0] + " 书号：" + args1[1] + " 还书日期：" + Integer.toString(date.getYear()) + "年" + Integer.toString(date.getMonthValue()) + "月" + Integer.toString(date.getDayOfMonth()) + "日");
                    }
                    ;
                } else if (com == 9) {
                    System.out.println("查询借书记录，请输入借书证号");
                    command = scanner.nextLine();
                    ApiResult apiResult = library.showBorrowHistory(Integer.parseInt(command));
                    if (!apiResult.ok) System.out.println("查询失败!");
                    else {
                        BorrowHistories histories = (BorrowHistories) apiResult.payload;
                        System.out.println("借书证号：" + command + " 借书记录如下:");
                        for (BorrowHistories.Item borrow : histories.getItems()) {
                            long timestamp = borrow.getBorrowTime(); // 毫秒级时间戳，例如：2024-03-29 12:00:05

                            // 使用 Instant.ofEpochMilli 方法将毫秒级时间戳转换为 Instant 类型
                            Instant instant = Instant.ofEpochMilli(timestamp);

                            // 使用 Instant 转换为 LocalDateTime 对象
                            LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

                            System.out.println("书号：" + borrow.getBookId() + " 借书时间：" + localDateTime);
                        }
                    }
                } else if (com == 10) {
                    System.out.println("我们支持以下查询方式:<类别查询(模糊查询),书名查询(模糊查询)， 出版社查询(模糊查询)，出版社查询(模糊查询)，年份范围查询，作者查询(模糊查询)，价格范围差>");
                    System.out.println("请输入查询方式，以及对应的参数，参数之间以逗号分隔<category, title, Press, MinPublishYear, MaxPublishYear, Author, MinPrice, MaxPrice>");
                    command = scanner.nextLine();
                    String[] args1 = command.split(",");
                    BookQueryConditions conditions = new BookQueryConditions();
                    if (!Objects.equals(args1[0], " ")) conditions.setCategory(args1[0]);
                    if (!Objects.equals(args1[1], " ")) conditions.setTitle(args1[1]);
                    if (!Objects.equals(args1[2], " ")) conditions.setPress(args1[2]);
                    if (!Objects.equals(args1[3], " ")) conditions.setMinPublishYear(Integer.parseInt(args1[3]));
                    if (!Objects.equals(args1[4], " ")) conditions.setMaxPublishYear(Integer.parseInt(args1[4]));
                    if (!Objects.equals(args1[5], " ")) conditions.setAuthor(args1[5]);
                    //System.out.println(args1[6]);
                    if (!Objects.equals(args1[6], " ")) conditions.setMinPrice(Double.parseDouble(args1[6]));
                    if (!Objects.equals(args1[7], " ")) conditions.setMaxPrice(Double.parseDouble(args1[7]));
                    ApiResult apiResult = library.queryBook(conditions);
                    BookQueryResults results = (BookQueryResults) apiResult.payload;
                    if (!apiResult.ok) System.out.println("查询失败!" + apiResult.message);
                    else {
                        System.out.println("查询结果如下:" + Integer.toString(results.getCount()));
                        for (Book book : results.getResults()) {
                            System.out.println("书号：" + book.getBookId() + " 类别：" + book.getCategory() + " 书名：" + book.getTitle() + " 出版社：" + book.getPress() + " 年份：" + Integer.toString(book.getPublishYear()) + " 作者：" + book.getAuthor() + " 价格：" + Double.toString(book.getPrice()) + " 库存：" + book.getStock());
                        }
                    }
                }
                System.out.println("输入1-10中的一个数字来启动对应的功能, 输入-1结束交互");
                System.out.println("1.添加图书");
                System.out.println("2.修改图书余量");
                System.out.println("3.修改图书信息");
                System.out.println("4.批量入库图书");
                System.out.println("5.注册借书证");
                System.out.println("6.查询借书证");
                System.out.println("7.借书");
                System.out.println("8.还书");
                System.out.println("9.查询借书记录");
                System.out.println("10.查询图书信息");
                command = scanner.nextLine();
                com = Integer.parseInt(command);
            }
            // release database connection handler
            if (connector.release()) {
                log.info("Success to release connection.");
            } else {
                log.warning("Failed to release connection.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class CardHandler implements HttpHandler {
        // 关键重写handle方法
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // 允许所有域的请求，cors处理
//            Headers headers = exchange.getResponseHeaders();
//            headers.add("Access-Control-Allow-Origin", "*");
//            headers.add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
//            headers.add("Access-Control-Allow-Headers", "Content-Type");
//            // 解析请求的方法，看GET还是POST
//            String path = exchange.getRequestURI().getPath();
//            String requestMethod = exchange.getRequestMethod();
//
//            if (requestMethod.equals("OPTIONS")) {
//                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
//                exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
//                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
//                exchange.sendResponseHeaders(204, 0);
//            }
//            System.out.println(path+requestMethod);
//            // 注意判断要用equals方法而不是==啊，java的小坑（
//            if (path.equals("/card")) {
//                System.out.println(path+requestMethod+"666");
//                if (requestMethod.equals("GET")) {
//                    // 处理GET
//                    handleGetCardRequest(exchange);
//                } else if (requestMethod.equals("POST")) {
//                    // 处理POST
//                    handlePostCardRequest(exchange);
//                }
//            } else if (path.equals("/card/remove")) {
//                if (requestMethod.equals("POST")) {
//                    handleRemoveCardRequest(exchange);
//                }
//            }

            Headers headers = exchange.getResponseHeaders();
            headers.add("Access-Control-Allow-Origin", "*");
            headers.add("Access-Control-Allow-Methods", "GET, POST");
            headers.add("Access-Control-Allow-Headers", "Content-Type");
            // 解析请求的方法，看GET还是POST
            String requestMethod = exchange.getRequestMethod();
            // 注意判断要用equals方法而不是==啊，java的小坑（
            if (requestMethod.equals("GET")) {
                // 处理GET
                handleGetCardRequest(exchange);
            } else if (requestMethod.equals("POST")) {
                // 处理POST
                handlePostCardRequest(exchange);
            } else if (requestMethod.equals("OPTIONS")) {
                // 处理OPTIONS
                handleOptionsRequest(exchange);
            } else {
                // 其他请求返回405 Method Not Allowed
                exchange.sendResponseHeaders(405, -1);
            }
        }

        private void handleGetCardRequest(HttpExchange exchange) throws IOException {

            List<Card> cards = ((CardList) library.showCards().payload).getCards();
//            for(Card card: cards){
//                System.out.println("姓名：" + card.getName() + " 单位：" + card.getDepartment() + " 身份：" + card.getType() + " 编号：" + card.getCardId());
//            }
            // 创建一个 JSON 数组，用于存储所有卡片信息
            JSONArray jsonArray = new JSONArray();

            // 遍历每张卡片，将其转换为 JSON 对象，并添加到 JSON 数组中
            for (Card card : cards) {
                JSONObject cardJson = new JSONObject();
                cardJson.put("name", card.getName());
                cardJson.put("department", card.getDepartment());
                cardJson.put("type", card.getType());
                cardJson.put("id", card.getCardId());
                jsonArray.put(cardJson);
            }
            // 将 JSON 数组转换为字符串
            String jsonResponse = jsonArray.toString();
            // 响应头，因为是JSON通信
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            // 状态码为200，也就是status ok
            exchange.sendResponseHeaders(200, 0);
            // 获取输出流，java用流对象来进行io操作
            OutputStream outputStream = exchange.getResponseBody();
            // 构建JSON响应数据，这里简化为字符串
            // 这里写的一个固定的JSON，实际可以查表获取数据，然后再拼出想要的JSON
//            String response = ;
//
//
//            // 写
//            outputStream.write(response.getBytes());
            outputStream.write(jsonResponse.getBytes());
            // 流一定要close！！！小心泄漏
            outputStream.close();
        }

        private void handleRemoveCardRequest(HttpExchange exchange) throws IOException {
            System.out.println("666");
            // 读取POST请求体
            InputStream requestBody = exchange.getRequestBody();
            // 用这个请求体（输入流）构造个buffered reader
            BufferedReader reader = new BufferedReader(new InputStreamReader(requestBody));
            // 拼字符串的
            StringBuilder requestBodyBuilder = new StringBuilder();
            // 用来读的
            String line;
            // 没读完，一直读，拼到string builder里
            while ((line = reader.readLine()) != null) {
                requestBodyBuilder.append(line);
            }

            // 看看读到了啥
            // 实际处理可能会更复杂点
            System.out.println("Received POST request to create card with data: " + requestBodyBuilder.toString());

            JSONObject json = new JSONObject(requestBodyBuilder.toString());

            // 提取姓名、部门和类型信息
//            String name = json.getString("name");
//            String department = json.getString("department");
//            String type = "S";
//            if (json.getString("type").equals("Teacher"))
//                type = "T";
//
//            // 打印提取的信息
//            System.out.println("Name: " + name);
//            System.out.println("Department: " + department);
//            System.out.println("Type: " + type);
            //System.out.println("请输入你想要注册的借书证的姓名，单位以及身份，身份只能为T(Teacher) 或者 S(Student)");
            //requestBodyBuilder.toString()
            //command = scanner.nextLine();
            //String[] args1 = requestBodyBuilder.toString().split(",");
//            entities.Card card = new Card();
//            card.setName(name);
//            card.setDepartment(department);
//            card.setType(Card.CardType.values(type));
//            ApiResult apiResult = library.registerCard(card);
//
//            String N;
//            if(json.getString("type").equals("Teacher")) N = "老师";
//            else N = "学生";
//            String number = ((Integer)apiResult.payload).toString();
//            if(!apiResult.ok) System.out.println("注册失败!" + apiResult.message);
//            else System.out.println(N + name + "注册成功!" + number);

            // 响应头
            exchange.getResponseHeaders().set("Content-Type", "text/plain");
            // 响应状态码200
            exchange.sendResponseHeaders(200, 0);

            // 剩下三个和GET一样
            OutputStream outputStream = exchange.getResponseBody();
            outputStream.write("Card created successfully".getBytes());
            outputStream.close();
        }

        private void handleOptionsRequest(HttpExchange exchange) throws IOException {
            exchange.sendResponseHeaders(204, 0);
        }

        private void handlePostCardRequest(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            // 读取POST请求体
            System.out.println(path);
            InputStream requestBody = exchange.getRequestBody();
            // 用这个请求体（输入流）构造个buffered reader
            BufferedReader reader = new BufferedReader(new InputStreamReader(requestBody));
            // 拼字符串的
            StringBuilder requestBodyBuilder = new StringBuilder();
            // 用来读的
            String line;
            // 没读完，一直读，拼到string builder里
            while ((line = reader.readLine()) != null) {
                requestBodyBuilder.append(line);
            }

            // 看看读到了啥
            // 实际处理可能会更复杂点
            System.out.println("Received POST request to create card with data: " + requestBodyBuilder.toString());

            JSONObject json = new JSONObject(requestBodyBuilder.toString());
            if (path.equals("/card")) {
                String name = json.getString("name");
                String department = json.getString("department");
                String type = "S";
                if (json.getString("type").equals("Teacher"))
                    type = "T";

                // 打印提取的信息
                System.out.println("Name: " + name);
                System.out.println("Department: " + department);
                System.out.println("Type: " + type);

                entities.Card card = new Card();
                card.setName(name);
                card.setDepartment(department);
                card.setType(Card.CardType.values(type));
                ApiResult apiResult = library.registerCard(card);
//
                String N;
                if (json.getString("type").equals("Teacher")) N = "老师";
                else N = "学生";
                String number = ((Integer) apiResult.payload).toString();
                if (!apiResult.ok) System.out.println("注册失败!" + apiResult.message);
                else System.out.println(N + name + "注册成功!" + number);
            } else if (path.equals("/card/remove")) {
                System.out.println("fuck");
                int id = json.getInt("id");
                System.out.println(id);
                ApiResult apiresult = library.removeCard(id);
                System.out.println("done");
            }
            // 提取姓名、部门和类型信息

            //System.out.println("请输入你想要注册的借书证的姓名，单位以及身份，身份只能为T(Teacher) 或者 S(Student)");
            //requestBodyBuilder.toString()
            //command = scanner.nextLine();
            //String[] args1 = requestBodyBuilder.toString().split(",");


            // 响应头
            exchange.getResponseHeaders().set("Content-Type", "text/plain");
            // 响应状态码200
            exchange.sendResponseHeaders(200, 0);

            // 剩下三个和GET一样
            OutputStream outputStream = exchange.getResponseBody();
            outputStream.write("Card created successfully".getBytes());
            outputStream.close();
        }
    }

    static class BookHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // 允许所有域的请求，cors处理
            Headers headers = exchange.getResponseHeaders();
            headers.add("Access-Control-Allow-Origin", "*");
            headers.add("Access-Control-Allow-Methods", "GET, POST");
            headers.add("Access-Control-Allow-Headers", "Content-Type");
            // 解析请求的方法，看GET还是POST
            String requestMethod = exchange.getRequestMethod();
            // 注意判断要用equals方法而不是==啊，java的小坑（
            if (requestMethod.equals("GET")) {
                // 处理GET
                handleGetRequest(exchange);
            } else if (requestMethod.equals("POST")) {
                // 处理POST
                handlePostRequest(exchange);
            } else if (requestMethod.equals("OPTIONS")) {
                // 处理OPTIONS
                handleOptionsRequest(exchange);
            } else {
                // 其他请求返回405 Method Not Allowed
                exchange.sendResponseHeaders(405, -1);
            }
        }

        private void handleGetRequest(HttpExchange exchange) throws IOException {
            // 在这里编写处理 GET 请求的代码
            BookQueryConditions conditions = new BookQueryConditions();
            ApiResult apiResult = library.queryBook(conditions);
            BookQueryResults results = (BookQueryResults) apiResult.payload;
            if (!apiResult.ok) System.out.println("查询失败!" + apiResult.message);
            else {
                System.out.println("查询结果如下:" + Integer.toString(results.getCount()));
//                for(Book book: results.getResults()){
//                    System.out.println("书号：" + book.getBookId() + " 类别：" + book.getCategory() + " 书名：" + book.getTitle() + " 出版社：" + book.getPress() + " 年份：" + Integer.toString(book.getPublishYear()) + " 作者：" + book.getAuthor() + " 价格：" + Double.toString(book.getPrice()) + " 库存：" + book.getStock());
//                }

            }

            List<Book> books = results.getResults();
//            for(Card card: cards){
//                System.out.println("姓名：" + card.getName() + " 单位：" + card.getDepartment() + " 身份：" + card.getType() + " 编号：" + card.getCardId());
//            }
            // 创建一个 JSON 数组，用于存储所有卡片信息
            JSONArray jsonArray = new JSONArray();

            // 遍历每张卡片，将其转换为 JSON 对象，并添加到 JSON 数组中
            for (Book book : books) {
                JSONObject bookJson = new JSONObject();
                bookJson.put("book_id", book.getBookId());
                bookJson.put("category", book.getCategory());
                bookJson.put("title", book.getTitle());
                bookJson.put("press", book.getPress());
                bookJson.put("publish_year", book.getPublishYear());
                bookJson.put("author", book.getAuthor());
                bookJson.put("price", book.getPrice());
                bookJson.put("stock", book.getStock());
                jsonArray.put(bookJson);
            }

            String jsonResponse = jsonArray.toString();

//
//
//            // 写
//            outputStream.write(response.getBytes());

            // 响应头，因为是JSON通信
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            // 状态码为200，也就是status ok
            exchange.sendResponseHeaders(200, 0);
            // 获取输出流，java用流对象来进行io操作
            OutputStream outputStream = exchange.getResponseBody();
            // 构建JSON响应数据，这里简化为字符串
            // 这里写的一个固定的JSON，实际可以查表获取数据，然后再拼出想要的JSON
//            //String response = "[{\"id\":1, \"category\" : \"Computer Science\", \"title\": \"Database System Concepts\", \"press\": \"Machine Industry Press\",\"publish_year\": 2023, \"author\": \"Mike\",\"price\": 188.88, \"stock\": 10}]";
//            // 写
//            outputStream.write(response.getBytes());
//            // 流一定要close！！！小心泄漏
            outputStream.write(jsonResponse.getBytes());
            outputStream.close();
        }

        private void handlePostRequest(HttpExchange exchange) throws IOException {
            // 在这里编写处理 POST 请求的代码
            String path = exchange.getRequestURI().getPath();
            // 读取POST请求体
            InputStream requestBody = exchange.getRequestBody();
            // 用这个请求体（输入流）构造个buffered reader
            BufferedReader reader = new BufferedReader(new InputStreamReader(requestBody));
            // 拼字符串的
            StringBuilder requestBodyBuilder = new StringBuilder();
            // 用来读的
            String line;
            // 没读完，一直读，拼到string builder里
            while ((line = reader.readLine()) != null) {
                requestBodyBuilder.append(line);
            }

            // 看看读到了啥
            // 实际处理可能会更复杂点
            System.out.println("Received POST request to create Book with data: " + requestBodyBuilder.toString());

            JSONObject json = new JSONObject(requestBodyBuilder.toString());

//            // 提取姓名、部门和类型信息
//            //int book_id = json.getInt("book_id");
//            String category = json.getString("category");
//            String title = json.getString("title");
//            String press = json.getString("press");
//            String author = json.getString("author");
//            int  stock = json.getInt("stock");
//            double price = json.getDouble("price");
//            int publish_year = json.getInt("publish_year");
            String returnMessage = "";
//            JSONObject json = new JSONObject(requestBody);
            if (path.equals("/book")) {
                String category = json.optString("category");
                String title = json.optString("title");
                String press = json.optString("press");
                String author = json.optString("author");
                int stock = json.optInt("stock", 0);
                double price = json.optDouble("price", 0.0);
                int publish_year = json.optInt("publish_year");

                entities.Book book = new Book(category, title, press, publish_year, author, price, stock);
                ApiResult apiResult = library.storeBook(book);
                if (!apiResult.ok) System.out.println("添加失败!" + apiResult.message);
                else
                    System.out.println("《" + title + "》" + "添加成功!  BookID = " + Objects.toString(apiResult.payload));
                returnMessage = "《" + title + "》" + "添加成功!  BookID = " + Objects.toString(apiResult.payload);
            } else if (path.equals("/book/modify")) {
                String category = json.optString("category");
                String title = json.optString("title");
                String press = json.optString("press");
                String author = json.optString("author");
                int stock = json.optInt("stock", 0);
                double price = json.optDouble("price", 0.0);
                int publish_year = json.optInt("publish_year");
                int book_id = json.getInt("book_id");

                entities.Book book = new Book(category, title, press, publish_year, author, price, stock);
                book.setBookId(book_id);
                ApiResult apiResult = library.modifyBookInfo(book);

                if (!apiResult.ok) System.out.println("修改失败!" + apiResult.message);
                else System.out.println("《" + title + "》" + "修改成功!  BookID = " + book_id);
                returnMessage = Objects.toString(apiResult.payload);
            } else if (path.equals("/book/remove")) {
                int book_id = json.getInt("book_id");
                ApiResult apiResult = library.removeBook(book_id);
                if (!apiResult.ok) System.out.println("删除失败!" + apiResult.message);
                else System.out.println("删除成功" + " BookID = " + book_id);
                returnMessage = Objects.toString(apiResult.payload);
            } else if (path.equals("/book/borrow")) {
                int book_id = json.getInt("bookId");
                int card_id = json.getInt("borrowerId");

                Borrow borrow = new Borrow(book_id, card_id);
                LocalDate date = LocalDate.now();
                borrow.resetBorrowTime();
                ApiResult apiResult = library.borrowBook(borrow);
                if (!apiResult.ok) System.out.println("借阅失败!" + apiResult.message);
                else System.out.println("借阅成功" + " BookID = " + book_id);
                returnMessage = Objects.toString(apiResult.payload);
            } else if (path.equals("/book/return")) {
                int book_id = json.getInt("bookId");
                int card_id = json.getInt("borrowerId");


                Borrow ret = new Borrow(book_id, card_id);
                LocalDate date = LocalDate.now();
                ret.resetReturnTime();
                ApiResult apiResult = library.returnBook(ret);
                if (!apiResult.ok) System.out.println("还书失败!" + apiResult.message);
                else System.out.println("还书成功" + " BookID = " + book_id);
//                returnMessage = Objects.toString(apiResult.payload);
                returnMessage = apiResult.message;
            }else if(path.equals("/book/modifyStock")){
                int book_id = Integer.parseInt(json.getString("bookId"));
                int stock = Integer.parseInt(json.getString("quantity"));
                ApiResult apiResult = library.incBookStock(book_id, stock);
                if (!apiResult.ok) System.out.println("修改失败!" + apiResult.message);
                else System.out.println("《" + book_id + "》" + "修改成功!" + "Stockdetal :" + stock);
                returnMessage = Objects.toString(apiResult.message);
            }else if(path.equals("/book/search")){
                BookQueryConditions conditions = new BookQueryConditions();
                if (!json.optString("category").equals("")) conditions.setCategory(json.optString("category"));
                if (!json.optString("title").equals("")) conditions.setTitle(json.optString("title"));
                if (!json.optString("press").equals("")) conditions.setPress(json.optString("press"));
                if (!json.optString("author").equals("")) conditions.setAuthor(json.optString("author"));
                if (!json.optString("minPublishYear").equals("")) conditions.setMinPublishYear(Integer.parseInt(json.optString("minPublishYear")));
                if (!json.optString("maxPublishYear").equals("")) conditions.setMaxPublishYear(Integer.parseInt(json.optString("maxPublishYear")));
                if (!json.optString("minPrice").equals("")) conditions.setMinPrice(Double.parseDouble(json.optString("minPrice")));
                if (!json.optString("maxPrice").equals("")) conditions.setMaxPrice(Double.parseDouble(json.optString("maxPrice")));
                ApiResult apiResult = library.queryBook(conditions);
                BookQueryResults results = (BookQueryResults) apiResult.payload;
                if (!apiResult.ok) System.out.println("查询失败!" + apiResult.message);
                else {
                    System.out.println("查询结果如下:" + Integer.toString(results.getCount()));
                }
                List<Book> books = results.getResults();
                JSONArray jsonArray = new JSONArray();
                for (Book book : books) {
                    JSONObject bookJson = new JSONObject();
                    bookJson.put("book_id", book.getBookId());
                    bookJson.put("category", book.getCategory());
                    bookJson.put("title", book.getTitle());
                    bookJson.put("press", book.getPress());
                    bookJson.put("publish_year", book.getPublishYear());
                    bookJson.put("author", book.getAuthor());
                    bookJson.put("price", book.getPrice());
                    bookJson.put("stock", book.getStock());
                    jsonArray.put(bookJson);
                }
                returnMessage = jsonArray.toString();
                System.out.println(returnMessage);
            }else if(path.equals("/book/import")){
                String filename = json.optString("path");
                List<Book> books = new ArrayList<Book>();
                try (Scanner sc = new Scanner(new File(filename))) {
                    while (sc.hasNextLine()) {
                        String[] args1 = sc.nextLine().split(",");
                        entities.Book book = new Book(args1[1], args1[2], args1[3], Integer.parseInt(args1[4]), args1[5], Double.parseDouble(args1[6]), Integer.parseInt(args1[7]));
                        book.setBookId(Integer.parseInt(args1[0]));
                        books.add(book);
                    }
                }
                ApiResult apiResult = library.storeBook(books);
                if (!apiResult.ok) System.out.println("批量入库失败!" + apiResult.message);
                else System.out.println("批量入库成功!");
                returnMessage = apiResult.message;
            }

            // 提取姓名、部门和类型信息
//            String name = json.optString("name", "");
//            String department = json.optString("department", "");
//            String type = json.optString("type", "");
//            int stock = json.optInt("stock", 0);
//            double price = json.optDouble("price", 0.0);
//            int publish_year = json.optInt("publish_year", 0);
//            entities.Book book = new Book();
//
//            book.setStock(stock);
//            book.setAuthor(author);
//            book.setTitle(title);
//            book.setCategory(category);
//            //book.setBookId(book_id);
//            book.setPress(press);
//            book.setPublishYear(publish_year);
//            book.setPrice(price);

            //System.out.println("请输入<类别，书名，出版社，年份，作者，价格，初始库存>(系统将自动分配书号), 属性之间以逗号分隔");
            //command = scanner.nextLine();
            //String[] args1 = command.split(",");
            //System.out.println(args1[7]);
            // 响应头
            exchange.getResponseHeaders().set("Content-Type", "text/plain");
            // 响应状态码200
            exchange.sendResponseHeaders(200, 0);

            // 剩下三个和GET一样
            OutputStream outputStream = exchange.getResponseBody();
            //outputStream.write("Book created successfully".getBytes());
            outputStream.write(returnMessage.getBytes());
            outputStream.close();
        }

        private void handleOptionsRequest(HttpExchange exchange) throws IOException {
            // 在这里编写处理 OPTIONS 请求的代码
            exchange.sendResponseHeaders(204, 0);
        }
    }


    static class BorrowHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // 允许所有域的请求，cors处理
            Headers headers = exchange.getResponseHeaders();
            headers.add("Access-Control-Allow-Origin", "*");
            headers.add("Access-Control-Allow-Methods", "GET, POST");
            headers.add("Access-Control-Allow-Headers", "Content-Type");
            // 解析请求的方法，看GET还是POST
            String requestMethod = exchange.getRequestMethod();
            // 注意判断要用equals方法而不是==啊，java的小坑（
            if (requestMethod.equals("GET")) {
                // 处理GET
                handleGetRequest(exchange);
            } else if (requestMethod.equals("POST")) {
                // 处理POST
                handlePostRequest(exchange);
            } else if (requestMethod.equals("OPTIONS")) {
                // 处理OPTIONS
                handleOptionsRequest(exchange);
            } else {
                // 其他请求返回405 Method Not Allowed
                exchange.sendResponseHeaders(405, -1);
            }
        }

        private void handleGetRequest(HttpExchange exchange) throws IOException {
//            Map<String, String> params = parseQueryString(exchange.getRequestURI().getQuery());
//            String cardID = params.get("cardID");
            // 在这里编写处理 GET 请求的代码

            // 响应头，因为是JSON通信
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            // 状态码为200，也就是status ok
            exchange.sendResponseHeaders(200, 0);
            // 获取输出流，java用流对象来进行io操作
            OutputStream outputStream = exchange.getResponseBody();
            // 构建JSON响应数据，这里简化为字符串
            // 这里写的一个固定的JSON，实际可以查表获取数据，然后再拼出想要的JSON
            String response = "[{\"cardID\": 1, \"bookID\" : 1, \"borrowTime\": 20240401, \"returnTime\": 20240423}]";
            // 写
            outputStream.write(response.getBytes());
            // 流一定要close！！！小心泄漏
            outputStream.close();
        }

        private void handlePostRequest(HttpExchange exchange) throws IOException {
            // 在这里编写处理 POST 请求的代码
//            System.out.println("查询借书记录，请输入借书证号");
//            //command = scanner.nextLine();
//            ApiResult apiResult = library.showBorrowHistory(Integer.parseInt(command));
//            if(!apiResult.ok) System.out.println("查询失败!");
//            else{
//                BorrowHistories histories = (BorrowHistories) apiResult.payload;
//                System.out.println("借书证号：" + command + " 借书记录如下:");
//                for(BorrowHistories.Item borrow: histories.getItems()){
//                    long timestamp = borrow.getBorrowTime(); // 毫秒级时间戳，例如：2024-03-29 12:00:05
//
//                    // 使用 Instant.ofEpochMilli 方法将毫秒级时间戳转换为 Instant 类型
//                    Instant instant = Instant.ofEpochMilli(timestamp);
//
//                    // 使用 Instant 转换为 LocalDateTime 对象
//                    LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
//
//                    System.out.println("书号：" + borrow.getBookId() + " 借书时间：" + localDateTime);
//                }
//            }
            // 读取POST请求体
            InputStream requestBody = exchange.getRequestBody();
            // 用这个请求体（输入流）构造个buffered reader
            BufferedReader reader = new BufferedReader(new InputStreamReader(requestBody));
            // 拼字符串的
            StringBuilder requestBodyBuilder = new StringBuilder();
            // 用来读的
            String line;
            // 没读完，一直读，拼到string builder里
            while ((line = reader.readLine()) != null) {
                requestBodyBuilder.append(line);
            }

            // 看看读到了啥
            // 实际处理可能会更复杂点
            System.out.println("Received POST request to create Borrow with data: " + requestBodyBuilder.toString());
            JSONObject json = new JSONObject(requestBodyBuilder.toString());

//            // 提取姓名、部门和类型信息
//            //int book_id = json.getInt("book_id");
//            String category = json.getString("category");
//            String title = json.getString("title");
//            String press = json.getString("press");
//            String author = json.getString("author");
//            int  stock = json.getInt("stock");
//            double price = json.getDouble("price");
//            int publish_year = json.getInt("publish_year");
            String returnMessage = "";

            int card_id = Integer.parseInt(json.getString("cardID"));

            ApiResult apiResult = library.showBorrowHistory(card_id);
//            if(!apiResult.ok) System.out.println("查询失败!");
//            else{
//                BorrowHistories histories = (BorrowHistories) apiResult.payload;
//                System.out.println("借书证号：" + card_id + " 借书记录如下:");
//                for(BorrowHistories.Item borrow: histories.getItems()){
//                    long timestamp = borrow.getBorrowTime(); // 毫秒级时间戳，例如：2024-03-29 12:00:05
//
//                    // 使用 Instant.ofEpochMilli 方法将毫秒级时间戳转换为 Instant 类型
//                    Instant instant = Instant.ofEpochMilli(timestamp);
//
//                    // 使用 Instant 转换为 LocalDateTime 对象
//                    LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
//
//                    System.out.println("书号：" + borrow.getBookId() + " 借书时间：" + localDateTime);
//                }
//            }
//            String response = "[{\"cardID\": 1, \"bookID\" : 1, \"borrowTime\": 20240401, \"returnTime\": 20240423}]";
//            OutputStream outputStream = exchange.getResponseBody();
//            // 写
//            outputStream.write(response.getBytes());
//                returnMessage = Objects.toString(apiResult.payload);


//            if (!apiResult.ok) {
//                System.out.println("查询失败!");
//            } else {
//                BorrowHistories histories = (BorrowHistories) apiResult.payload;
//                System.out.println("借书证号：" + card_id + " 借书记录如下:");
//
//                // 创建一个用于存储所有借书记录的列表
//                List<String> borrowRecords = new ArrayList<>();
//
//                for (BorrowHistories.Item borrow : histories.getItems()) {
////                    long timestamp = borrow.getBorrowTime(); // 毫秒级时间戳，例如：2024-03-29 12:00:05
////
////                    // 使用 Instant.ofEpochMilli 方法将毫秒级时间戳转换为 Instant 类型
////                    Instant instant = Instant.ofEpochMilli(timestamp);
////
////                    // 使用 Instant 转换为 LocalDateTime 对象
////                    LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
//                    long timestamp = borrow.getBorrowTime(); // 毫秒级时间戳，例如：2024-03-29 12:00:05
//
//                    // 使用 Instant.ofEpochMilli 方法将毫秒级时间戳转换为 Instant 类型
//                    Instant instant = Instant.ofEpochMilli(timestamp);
//
//                    // 使用 Instant 转换为 LocalDateTime 对象
//                    LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
//
//                    // 按照特定格式构建每个借书记录的 JSON 字符串，并添加到列表中
//                    String record = String.format("{\"cardID\": %d, \"bookID\" : %d, \"borrowTime\": %d, \"returnTime\": %d}", card_id, borrow.getBookId(), borrow.getBorrowTime(), borrow.getReturnTime());
//                    System.out.println(record);
//                    borrowRecords.add(record);
//                }
            List<String> borrowRecords = new ArrayList<>();
            if (!apiResult.ok) {
                System.out.println("查询失败!");
            } else {
                BorrowHistories histories = (BorrowHistories) apiResult.payload;
                System.out.println("借书证号：" + card_id + " 借书记录如下:");

                // 创建一个用于存储所有借书记录的列表


                for (BorrowHistories.Item borrow : histories.getItems()) {
                    // 使用 DateTimeFormatter 格式化时间
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime borrowTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(borrow.getBorrowTime()), ZoneId.systemDefault());
                    LocalDateTime returnTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(borrow.getReturnTime()), ZoneId.systemDefault());

                    // 按照特定格式构建每个借书记录的 JSON 字符串，并添加到列表中
                    String record = String.format("{\"cardID\": %d, \"bookID\" : %d, \"borrowTime\": \"%s\", \"returnTime\": \"%s\"}", card_id, borrow.getBookId(), formatter.format(borrowTime), formatter.format(returnTime));
                    System.out.println(record);
                    borrowRecords.add(record);
                }
            }

            // 将借书记录列表转换为 JSON 数组字符串
            String jsonResponse = "[" + String.join(",", borrowRecords) + "]";

            //System.out.println(jsonResponse); // 打印 JSON 响应

            //OutputStream outputStream = exchange.getResponseBody();
            // 写入 JSON 响应
            //outputStream.write(jsonResponse.getBytes());
            returnMessage = apiResult.message;
            // 响应头
            //exchange.getResponseHeaders().set("Content-Type", "text/plain");
            // 响应状态码200
            //exchange.sendResponseHeaders(200, 0);
            // 剩下三个和GET一样
            //outputStream.write(returnMessage.getBytes());
            //outputStream.close();




            exchange.getResponseHeaders().set("Content-Type", "application/json");
            // 状态码为200，也就是status ok
            exchange.sendResponseHeaders(200, 0);
            // 获取输出流，java用流对象来进行io操作
            OutputStream outputStream = exchange.getResponseBody();
            // 构建JSON响应数据，这里简化为字符串
            // 这里写的一个固定的JSON，实际可以查表获取数据，然后再拼出想要的JSON
            //String response = "[{\"cardID\": 1, \"bookID\" : 1, \"borrowTime\": 20240401, \"returnTime\": 20240423}]";
            // 写
            outputStream.write(jsonResponse.getBytes());
            // 流一定要close！！！小心泄漏
            outputStream.close();
        }
    }

        private static void handleOptionsRequest(HttpExchange exchange) throws IOException {
            // 在这里编写处理 OPTIONS 请求的代码
            exchange.sendResponseHeaders(204, 0);
        }
    }

