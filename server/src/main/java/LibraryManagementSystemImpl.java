import entities.Book;
import entities.Borrow;
import entities.Card;
import queries.*;
import utils.DBInitializer;
import utils.DatabaseConnector;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

/*
mvn -Dtest=LibraryTest# clean test
mvn -Dtest=LibraryTest#borrowAndReturnBookTest clean test
mvn -Dtest=LibraryTest clean test
*/

public class LibraryManagementSystemImpl implements LibraryManagementSystem {

    private final DatabaseConnector connector;

    public LibraryManagementSystemImpl(DatabaseConnector connector) {
        this.connector = connector;
    }

    @Override
    public ApiResult storeBook(Book book) {
        Connection conn=connector.getConn();
        try{
            conn.setAutoCommit(false);
            Statement stmt=conn.createStatement();
            String sql="SELECT book_id FROM book WHERE "+"category = '"+book.getCategory()+
            "' AND title = '"+book.getTitle()+"' AND press = '"+book.getPress()+"' AND publish_year = "+
            book.getPublishYear()+" AND author = '"+book.getAuthor()+"';";
            ResultSet res=stmt.executeQuery(sql);
            if(res.next())
            {
                return new ApiResult(false,"The book already exists.");
            }
            try{
                String sql1="INSERT INTO book (category, title, press, publish_year, author, price, stock)"+
                "VALUES ('"+book.getCategory()+"', '"+book.getTitle()+"', '"+book.getPress()+"', "+book.getPublishYear()+
                ", '"+book.getAuthor()+"', "+book.getPrice()+", "+book.getStock()+") ;";
                stmt.executeUpdate(sql1);
            }catch(SQLException sqlexception)
            {
                System.out.println("Could not insert "+sqlexception);
            }
            res=stmt.executeQuery(sql);
            if(res.next())
            {
                book.setBookId(res.getInt("book_id"));
            }
            commit(conn);
        }
        catch(Exception e)
        {
            rollback(conn);
            return new ApiResult(false,e.getMessage());
        }
        return new ApiResult(true, "storeBook(Book book) success");
    }

    @Override
    public ApiResult incBookStock(int bookId, int deltaStock) {
        Connection conn=connector.getConn();
        try{
            conn.setAutoCommit(false);
            PreparedStatement pstmt=null;
            String sql="SELECT stock\n"+"FROM book\n"+
            "WHERE book_id = ? ;\n";
            pstmt=conn.prepareStatement(sql);
            pstmt.setInt(1,bookId);
            ResultSet res=pstmt.executeQuery();
            if(res.next())
            {
                int stock=res.getInt("stock");
                if(stock+deltaStock<0)
                {
                    throw new Exception("The stock can't benegative.");
                }
                else{
                    String sql1="UPDATE book\n"+"SET stock = ?\n"+
                    "WHERE book_id = ? ;\n";
                    pstmt=conn.prepareStatement(sql1);
                    pstmt.setInt(1,stock+deltaStock);
                    pstmt.setInt(2,bookId);
                    pstmt.executeUpdate();
                    commit(conn);
                }
            }else{
                throw new Exception("The book doesn't exist in the library system.");
            }
        }catch(Exception e)
        {
            rollback(conn);
            return new ApiResult(false,e.getMessage());
        }
        return new ApiResult(true, "incBookStock(int bookId,int deltaStock) success");
    }

    @Override
    public ApiResult storeBook(List<Book> books) {
        Connection conn=connector.getConn();
        try{
            conn.setAutoCommit(false);
            String sql="SELECT book_id FROM book WHERE catagory = ? AND title = ? AND press = ? AND publish_year = ? AND author = ?;";
            PreparedStatement pstmt=conn.prepareStatement(sql);
            String sql1="INSERT INTO book (category, title, press, publish_year, author, price, stock) VALUES(?, ?, ?, ?, ?, ?, ?);";
            PreparedStatement pstmt1=conn.prepareStatement(sql1);
            for(Book temp:books)
            {
                pstmt.setString(1,temp.getCategory());
                pstmt.setString(2,temp.getTitle());
                pstmt.setString(3,temp.getPress());
                pstmt.setInt(4,temp.getPublishYear());
                pstmt.setString(5,temp.getAuthor());
                pstmt.setDouble(6,temp.getPrice());
                pstmt.setInt(7,temp.getStock());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            for(Book temp:books)
            {
                pstmt1.setString(1,temp.getCategory());
                pstmt1.setString(2,temp.getTitle());
                pstmt1.setString(3,temp.getPress());
                pstmt1.setInt(4,temp.getPublishYear());
                pstmt1.setString(5,temp.getAuthor());
                ResultSet res=pstmt1.executeQuery();
                if(res.next())
                {
                    temp.setBookId(res.getInt("book_id"));
                }
            }
            commit(conn);
        }catch(Exception e)
        {
            rollback(conn);
            return new ApiResult(false,e.getMessage());
        }
        return new ApiResult(true, "storeBook(List<book> books) success");
    }

    @Override
    public ApiResult removeBook(int bookId) {
        Connection conn=connector.getConn();
        try{
            conn.setAutoCommit(false);
            PreparedStatement pstmt=null;
            String sql="SELECT *\n"+"FROM book\n"+
            "WHERE book_id = ? ; ";
            pstmt=conn.prepareStatement(sql);
            pstmt.setInt(1,bookId);
            ResultSet res=pstmt.executeQuery();
            if(!res.next())
            {
                throw new Exception("The book doesn't exist.");
            }
            sql="SELECT book_id\n"+"FROM borrow\n"+"WHERE book_id = ? \n"+
            "AND return_time = 0 ;\n";
            pstmt=conn.prepareStatement(sql);
            pstmt.setInt(1,bookId);
            res=pstmt.executeQuery();
            if(res.next())
            {
                throw new Exception("There is someone who hasn't return this book.");
            }
            sql="DELETE FROM book\n"+"WHERE book_id = ? ; \n";
            pstmt=conn.prepareStatement(sql);
            pstmt.setInt(1,bookId);
            pstmt.executeUpdate();
            commit(conn);
        }catch(Exception e){
            rollback(conn);
            return new ApiResult(false,e.getMessage());
        }
        return new ApiResult(true, "removeBook(int bookId) success");
    }

    @Override
    public ApiResult modifyBookInfo(Book book) {
        Connection conn=connector.getConn();
        try{
            conn.setAutoCommit(false);
            PreparedStatement pstmt=null;
            String sql="SELECT *\n"+"FROM book\n"+"WHERE book_id = ? ; \n";
            pstmt=conn.prepareStatement(sql);
            pstmt.setInt(1,book.getBookId());
            ResultSet res=pstmt.executeQuery();
            if(!res.next())
            {
                throw new Exception("The book doesn't exist in the library.");
            }
            String sql1="UPDATE book\n"+"SET category = ?, title = ?, press = ?, publish_year = ?, author = ?, price = ? ; \n"+
            "WHERE book_id = ? ; \n";
            pstmt = conn.prepareStatement(sql1);
            pstmt.setString(1, book.getCategory());
            pstmt.setString(2, book.getTitle());
            pstmt.setString(3, book.getPress());
            pstmt.setInt(4, book.getPublishYear());
            pstmt.setString(5, book.getAuthor());
            pstmt.setDouble(6, book.getPrice());
            pstmt.setInt(7, book.getBookId());
            pstmt.executeUpdate();
            commit(conn);
        }catch(Exception e)
        {
            rollback(conn);
            return new ApiResult(false,e.getMessage());
        }
        return new ApiResult(true, "modifyBookInfo(Book book) success");
    }

    @Override
    public ApiResult queryBook(BookQueryConditions conditions) {
        Connection conn=connector.getConn();
        BookQueryResults bqRes=null;
        try{
            conn.setAutoCommit(false);
            PreparedStatement pstmt=null;
            String sql="SELECT *\n"+"FROM book\n"+"WHERE ";
            int flag=0;
            if(conditions.getCategory()!=null)
            {
                if(flag==0){
                    sql+="category = '"+conditions.getCategory()+"'";
                    flag=1;
                }
                else{
                    sql+="AND category = '"+conditions.getCategory()+"'";
                }
            }
            if(conditions.getTitle()!=null)
            {
                if(flag==0){
                    sql+="title like '%"+conditions.getTitle()+"%'";
                    flag=1;
                }else{
                    sql+="AND title like '%"+conditions.getTitle()+"%'";
                }
            }
            if(conditions.getPress()!=null)
            {
                if(flag==0){
                    sql+="press like '%"+conditions.getPress()+"%'";
                    flag=1;
                }else{
                    sql+="AND press like '%"+conditions.getPress()+"%'";                    }
            }
            if(conditions.getMinPublishYear()!=null)
            {
                if(flag==0){
                    sql+="publish_year >= "+String.valueOf(conditions.getMinPublishYear());
                    flag=1;
                }
                else{
                    sql+="AND publish_year >= "+String.valueOf(conditions.getMinPublishYear());
                }
            }
            if(conditions.getAuthor()!=null)
            {
                if(flag==0){
                    sql+="author like '%"+conditions.getAuthor()+"%'";
                    flag=1;
                }else{
                    sql+="AND author like '%"+conditions.getAuthor()+"%'";
                }
            }
            if(conditions.getMaxPrice()!=null)
            {
                if(flag==0){
                sql+="price <= "+String.valueOf(conditions.getMaxPrice());                        
                flag=1;
                }
                else{
                    sql+="AND price <= "+String.valueOf(conditions.getMaxPrice());
                }                
            }
            if(conditions.getMinPrice()!=null)
            {
                if(flag==0){
                sql=sql+"price >= "+String.valueOf(conditions.getMinPrice())+";";                        
                flag=1;
                }
                else{
                    sql=sql+"AND price >= "+String.valueOf(conditions.getMinPrice())+";";
                }
            }
            if(flag==0)
            {
                sql="SELECT *\n"+"FROM book ;";
            }
            if(conditions.getSortBy().equals(Book.SortColumn.BOOK_ID)){
                sql+="\nORDER BY "+conditions.getSortBy()+" "+conditions.getSortOrder()+" ;\n";
            }
            else{
                sql+="\nORDER BY "+conditions.getSortBy()+" "+conditions.getSortOrder()+", book_id ASC"+" ;\n";
            }
            pstmt=conn.prepareStatement(sql);
            ResultSet res=pstmt.executeQuery();
            List<Book> books=new ArrayList<>();                
            while(res.next())
            {
                Book book = new Book();
                book.setBookId(res.getInt("book_id"));
                book.setCategory(res.getString("category"));
                book.setTitle(res.getString("title"));
                book.setPress(res.getString("press"));
                book.setPublishYear(res.getInt("publish_year"));
                book.setAuthor(res.getString("author"));
                book.setPrice(res.getDouble("price"));
                book.setStock(res.getInt("stock"));
                books.add(book);               
            }
            bqRes=new BookQueryResults(books);
            commit(conn);
        }catch(Exception e)
        {
            rollback(conn);
            return new ApiResult(false,e.getMessage());
        }
        return new ApiResult(true, "queryBook(BookQueryConditions conditions) success",bqRes);
    }

    @Override
    public ApiResult borrowBook(Borrow borrow) {
        Connection conn=connector.getConn();
        try{
            PreparedStatement pstmt=null;
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            conn.setAutoCommit(false);
            String sql="SELECT *\n"+"FROM borrow\n"+
            "WHERE card_id = ? AND book_id = ? AND return_time = 0 ;\n";
            pstmt=conn.prepareStatement(sql);
            pstmt.setInt(1,borrow.getCardId());
            pstmt.setInt(2,borrow.getBookId());
            ResultSet res=pstmt.executeQuery();
            if(res.next())
            {
                throw new Exception("The customer borrowed this book but hasn't return.");
            }
            sql="SELECT *\n"+"FROM book\n"+"WHERE book_id = ? ; \n";
            pstmt=conn.prepareStatement(sql);
            pstmt.setInt(1, borrow.getBookId());
            res=pstmt.executeQuery();
            int stock=0;
            if(res.next())
            {
                stock=res.getInt("stock");
            }
            else{
                throw new Exception("The book doesn't exist.");
            }
            if(stock<=0)
            {
                throw new Exception("The book is out of stock.");
            }
            else{
                sql="UPDATE book\n"+"SET stock = stock - 1\n"+
                "WHERE book_id = ? AND stock = ? ; \n";
                pstmt=conn.prepareStatement(sql);
                pstmt.setInt(1, borrow.getBookId());
                pstmt.setInt(2, stock);
                pstmt.executeUpdate();
                String sql1="INSERT INTO borrow\n" +
                        "VALUES(?, ?, ?, ?); \n";
                pstmt=conn.prepareStatement(sql1);
                pstmt.setInt(1, borrow.getCardId());
                pstmt.setInt(2, borrow.getBookId());
                pstmt.setLong(3, borrow.getBorrowTime());
                pstmt.setLong(4, 0);
                pstmt.executeUpdate();
            }
            commit(conn);
        }catch(Exception e){
            rollback(conn);
            return new ApiResult(false,e.getMessage());
        }
        return new ApiResult(true,"modifyBookInfo(Book book) success");
    }

    @Override
    public ApiResult returnBook(Borrow borrow) {
        Connection conn=connector.getConn();
        try{
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            conn.setAutoCommit(false);
            PreparedStatement pstmt=null;
            String sql="SELECT *\n"+"FROM borrow\n"+
            "WHERE card_id = ? AND book_id = ? AND borrow_time = ? AND return_time = 0 ;\n";
            pstmt=conn.prepareStatement(sql);
            pstmt.setInt(1, borrow.getCardId());
            pstmt.setInt(2, borrow.getBookId());
            pstmt.setLong(3, borrow.getBorrowTime());
            ResultSet res=pstmt.executeQuery();
            if(!res.next())
            {
                throw new Exception("There is no borrow record or this book has been returned.");
            }
            else{
                String sql1="UPDATE borrow\n"+"SET return_time = ? ; \n"+
                "WHERE card_id = ? AND book_id = ? AND borrow_time = ? ; \n";
                pstmt=conn.prepareStatement(sql1);
                pstmt.setLong(1, borrow.getReturnTime());
                pstmt.setInt(2, borrow.getCardId());
                pstmt.setInt(3, borrow.getBookId());
                pstmt.setLong(4, borrow.getBorrowTime());
                pstmt.executeUpdate();
                sql="SELECT *\n"+"FROM book\n"+"WHERE book_id = ? ; \n";
                pstmt=conn.prepareStatement(sql);
                pstmt.setInt(1,borrow.getBookId());
                res=pstmt.executeQuery();
                int stock=0;
                if(res.next())
                {
                    stock=res.getInt("stock");
                }
                else{
                    throw new Exception("The book doesn't exist.");
                }
                sql="UPDATE book\n"+"SET stock = stock + 1\n"+"WHERE book_id = ? ;";
                pstmt=conn.prepareStatement(sql);
                pstmt.setInt(1,borrow.getBookId());
                pstmt.executeUpdate();
            }
            commit(conn);
        }catch(Exception e){
            rollback(conn);
            return new ApiResult(false,e.getMessage());
        }
        return new ApiResult(true,"returnBook(Borrow borrow) success");
    }

    @Override
    public ApiResult showBorrowHistory(int cardId) {
        Connection conn=connector.getConn();
        BorrowHistories borrowHistories=null;
        try{
            conn.setAutoCommit(false);
            PreparedStatement pstmt=null;
            String sql="SELECT *\n"+"FROM borrow\n"+"WHERE card_id = ? \n"+
            "ORDER BY borrow_time DESC, book_id ASC ;\n";
            pstmt=conn.prepareStatement(sql);
            pstmt.setInt(1,cardId);
            ResultSet res=pstmt.executeQuery();
            List<BorrowHistories.Item> items=new ArrayList<BorrowHistories.Item>();
            while(res.next())
            {
                int bookId=res.getInt("book_id");
                long borrowTime=res.getLong("borrow_time");
                long returnTime=res.getLong("return_time");
                sql="SELECT *\n"+"FROM book\n"+"WHERE book_id = ? ; \n";
                pstmt=conn.prepareStatement(sql);
                pstmt.setInt(1,bookId);
                ResultSet res1=pstmt.executeQuery();
                if(res1.next())
                {
                    String category=res1.getString("category");
                    String title=res1.getString("title");
                    String press=res1.getString("press");
                    int publishYear=res1.getInt("publish_year");
                    String author=res1.getString("author");
                    double price=res1.getDouble("price");
                    BorrowHistories.Item item=new BorrowHistories.Item();
                    item.setCardId(cardId);
                    item.setBookId(bookId);
                    item.setCategory(category);
                    item.setTitle(title);
                    item.setPress(press);
                    item.setPublishYear(publishYear);
                    item.setAuthor(author);
                    item.setPrice(price);
                    item.setBorrowTime(borrowTime);
                    item.setReturnTime(returnTime);
                    items.add(item);
                }
            }
            borrowHistories=new BorrowHistories(items);
            commit(conn);
        }catch(Exception e)
        {
            rollback(conn);
            return new ApiResult(false,e.getMessage());
        }
        return new ApiResult(true,"returnBook(Borrow borrow) success",borrowHistories);
    }

    @Override
    public ApiResult registerCard(Card card) {
        Connection conn=connector.getConn();
        try{
            conn.setAutoCommit(false);
            String sql="SELECT card_id FROM card "+
            "WHERE name = ? AND department = ? AND type = ?;";
            PreparedStatement pstmt=conn.prepareStatement(sql);
            pstmt.setString(1,card.getName());
            pstmt.setString(2,card.getDepartment());
            pstmt.setString(3,card.getType().getStr());
            ResultSet res=pstmt.executeQuery();
            if(res.next())
            {
                rollback(conn);
                return new ApiResult(false,"The card has existed.");
            }
            String sql1="INSERT INTO card(name, department, type), VALUES(?, ?, ?);";
            PreparedStatement pstmt1=conn.prepareStatement(sql1);
            pstmt1.setString(1,card.getName());
            pstmt1.setString(2,card.getDepartment());
            pstmt1.setString(3,card.getType().getStr());
            pstmt1.executeUpdate();
            res=pstmt.executeQuery();
            if(res.next())
            {
                card.setCardId(res.getInt("card_id"));
            }
            commit(conn);
        }catch(Exception e)
        {
            rollback(conn);
            return new ApiResult(false,e.getMessage());
        }
        return new ApiResult(true, "registerCard(Card card) success");
    }

    @Override
    public ApiResult removeCard(int cardId) {
        Connection conn=connector.getConn();
        try{
            conn.setAutoCommit(false);
            PreparedStatement pstmt=null;
            String sql="SELECT *\n"+"FROM card\n"+"WHERE card_id = ? ; ";
            pstmt=conn.prepareStatement(sql);
            pstmt.setInt(1, cardId);
            ResultSet res=pstmt.executeQuery();
            if(!res.next())
            {
                throw new Exception("The card doesn't exist.");
            }
            sql="SELECT *\n"+"FROM borrow\n"+
            "WHERE card_id = ? AND return_time = 0 ; \n";
            pstmt=conn.prepareStatement(sql);
            pstmt.setInt(1,cardId);
            res=pstmt.executeQuery();
            if(res.next())
            {
                throw new Exception("There is some book hasn't been returned.");
            }
            else{
                sql="DELETE FROM card\n"+"WHERE card_id = ? ; \n";
                pstmt=conn.prepareStatement(sql);
                pstmt.setInt(1,cardId);
                pstmt.executeUpdate();
            }
            commit(conn);
        }catch(Exception e)
        {
            rollback(conn);
            return new ApiResult(false,e.getMessage());
        }
        return new ApiResult(true,"removeCard(int cardId) success");
    }

    @Override
    public ApiResult showCards() {
        Connection conn=connector.getConn();
        CardList cardList=null;
        try{
            conn.setAutoCommit(false);
            PreparedStatement pstmt=null;
            String sql="SELECT *\n" +"FROM card\n" +
                    "ORDER BY card_id ; \n";
            pstmt=conn.prepareStatement(sql);
            ResultSet res=pstmt.executeQuery();
            List<Card> cards=new ArrayList<Card>();
            while(res.next()){
                int cardId=res.getInt("card_id");
                String name=res.getString("name");
                String department=res.getString("department");
                String type=res.getString("type");
                Card card=new Card(cardId, name, department, Card.CardType.values(type));
                cards.add(card);
            }
            cardList=new CardList(cards);
            commit(conn);
        }catch(Exception e){
            rollback(conn);
            return new ApiResult(false,e.getMessage());
        }
        return new ApiResult(true,"showCards() success",cardList);
    }

    @Override
    public ApiResult resetDatabase() {
        Connection conn = connector.getConn();
        try {
            Statement stmt = conn.createStatement();
            DBInitializer initializer = connector.getConf().getType().getDbInitializer();
            stmt.addBatch(initializer.sqlDropBorrow());
            stmt.addBatch(initializer.sqlDropBook());
            stmt.addBatch(initializer.sqlDropCard());
            stmt.addBatch(initializer.sqlCreateCard());
            stmt.addBatch(initializer.sqlCreateBook());
            stmt.addBatch(initializer.sqlCreateBorrow());
            stmt.executeBatch();
            commit(conn);
        } catch (Exception e) {
            rollback(conn);
            return new ApiResult(false, e.getMessage());
        }
        return new ApiResult(true, null);
    }

    private void rollback(Connection conn) {
        try {
            conn.rollback();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void commit(Connection conn) {
        try {
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
