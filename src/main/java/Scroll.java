import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Scroll {

    private String scrollId;
    private String name;
    private String uploaderId;
    private byte[] fileData;
    private String uploadDate;
    private String fileType;
    private int downloadCount;
    private int editCount;

    // Constructor with scrollID
    public Scroll(String scrollId, String name, String uploaderId, byte[] fileData, String uploadDate, String fileType, int downloadCount, int editCount) {
        this.scrollId = scrollId;
        this.name = name;
        this.uploaderId = uploaderId;
        this.fileData = fileData;
        this.uploadDate = uploadDate;
        this.fileType = fileType;
        this.downloadCount = downloadCount;
        this.editCount = editCount;
    }

    // Constructor without scrollID
    public Scroll(String name, String uploaderId, byte[] fileData, String uploadDate, String fileType, int downloadCount, int editCount) {

        // Generates scroll ID until unique key is created
        String idKey;
        while (true) {
            idKey = generateScrollKey();
            if (getScrollById(idKey) == null) {
                break;
            }
        }

        this.scrollId = idKey;
        this.name = name;
        this.uploaderId = uploaderId;
        this.fileData = fileData;
        this.uploadDate = uploadDate;
        this.fileType = fileType;
        this.downloadCount = downloadCount;
        this.editCount = editCount;
    }

    // Add scroll to the database
    public void saveToDatabase() {
        String sql = "INSERT INTO scrolls(scrollId, name, uploaderId, fileData, uploadDate, fileType, downloadCount, editCount) VALUES(?,?,?,?,?,?,?,?)";
        
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, scrollId);
            pstmt.setString(2, name);
            pstmt.setString(3, uploaderId);
            pstmt.setBytes(4, fileData);  
            pstmt.setString(5, uploadDate);
            pstmt.setString(6, fileType);
            pstmt.setInt(7, downloadCount);
            pstmt.setInt(8, editCount);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Method to update scroll
    public void updateScrollInDatabase() {
        String sql = "UPDATE scrolls SET name = ?, fileData = ?, uploadDate = ?, fileType = ?, downloadCount = ?, editCount = ? WHERE scrollId = ?";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setBytes(2, fileData);
            pstmt.setString(3, uploadDate);
            pstmt.setString(4, fileType);
            pstmt.setInt(5, downloadCount);
            pstmt.setInt(6, editCount);
            pstmt.setString(7, scrollId);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void incrementDownloadCount() {
        downloadCount++;
        updateScrollInDatabase();
    }

    public void incrementEditCount() {
        editCount++;
        updateScrollInDatabase();
    }

    public String getUploaderId() {
        return uploaderId;
    }

    public String getName() {
        return name;
    }

    public String getScrollId() {
        return scrollId;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] newData) {
        this.fileData = newData;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String newType) {
        this.fileType = newType;
    }


    public int getDownloadCount() {
        return downloadCount;
    }

    public int getEditCount() {
        return editCount;
    }

    public static List<Scroll> getAllScrollsFromDatabase() {
        String sql = "SELECT * FROM scrolls";
        List<Scroll> scrolls = new ArrayList<>();

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Scroll scroll = new Scroll(
                        rs.getString("scrollId"),
                        rs.getString("name"),
                        rs.getString("uploaderId"),
                        rs.getBytes("fileData"),
                        rs.getString("uploadDate"),
                        rs.getString("fileType"),
                        rs.getInt("downloadCount"),
                        rs.getInt("editCount")
                );
                scrolls.add(scroll);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return scrolls;
    }

    // Retrieve a scroll by its ID
    public static Scroll getScrollById(String scrollId) {
        String sql = "SELECT * FROM scrolls WHERE scrollId = ?";
        Scroll scroll = null;

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, scrollId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                scroll = new Scroll(
                        rs.getString("scrollId"),
                        rs.getString("name"),
                        rs.getString("uploaderId"),
                        rs.getBytes("fileData"),
                        rs.getString("uploadDate"),
                        rs.getString("fileType"),
                        rs.getInt("downloadCount"),
                        rs.getInt("editCount")
                );
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return scroll;
    }

    // Retrieve a scroll by its Name
    public static Scroll getScrollByName(String name) {
        String sql = "SELECT * FROM scrolls WHERE name = ?";
        Scroll scroll = null;

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                scroll = new Scroll(
                        rs.getString("scrollId"),
                        rs.getString("name"),
                        rs.getString("uploaderId"),
                        rs.getBytes("fileData"),
                        rs.getString("uploadDate"),
                        rs.getString("fileType"),
                        rs.getInt("downloadCount"),
                        rs.getInt("editCount")
                );
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return scroll;
    }

    //Scroll Filter
    public static List<Scroll> filterScrolls(ScrollFilter filter) {
            StringBuilder sql = new StringBuilder("SELECT * FROM scrolls WHERE 1=1");
            List<Object> parameters = new ArrayList<>();

            if (filter.getName() != null) {
                sql.append(" AND LOWER(name) LIKE LOWER(?)");
                parameters.add("%" + filter.getName() + "%");
            }

            if (filter.getUploaderId() != null) {
                sql.append(" AND uploaderId = ?");
                parameters.add(filter.getUploaderId());
            }

            if (filter.getFileType() != null) {
                sql.append(" AND fileType = ?");
                parameters.add(filter.getFileType());
            }

            if (filter.getStartDate() != null) {
                sql.append(" AND uploadDate >= ?");
                parameters.add(filter.getStartDate());
            }

            if (filter.getEndDate() != null) {
                sql.append(" AND uploadDate <= ?");
                parameters.add(filter.getEndDate());
            }

            if (filter.getMinDownloads() != null) {
                sql.append(" AND downloadCount >= ?");
                parameters.add(filter.getMinDownloads());
            }

            if (filter.getOrderBy() != null) {
                sql.append(" ORDER BY ").append(filter.getOrderBy());
                if (filter.isDescending()) {
                    sql.append(" DESC");
                }
            }

            List<Scroll> scrolls = new ArrayList<>();

            try (Connection conn = DatabaseHelper.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
                
                // Set parameters
                for (int i = 0; i < parameters.size(); i++) {
                    pstmt.setObject(i + 1, parameters.get(i));
                }

                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    Scroll scroll = new Scroll(
                        rs.getString("scrollId"),
                        rs.getString("name"),
                        rs.getString("uploaderId"),
                        rs.getBytes("fileData"),
                        rs.getString("uploadDate"),
                        rs.getString("fileType"),
                        rs.getInt("downloadCount"),
                        rs.getInt("editCount")
                    );
                    scrolls.add(scroll);
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

            return scrolls;
        }
    
    //this removes a scroll from the Scrolls database
    public static void removeFromDatabase(String scrollId) {
        String sqlQuery = "DELETE FROM scrolls WHERE scrollId = ?";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sqlQuery)) {

            pstmt.setString(1, scrollId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Generates a random scroll key for storage in the database. Scroll keys are 15 characters in length, and start with 'scroll-'
     * @return the random user key as a String.
     */
    private static String generateScrollKey() {
        String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder key = new StringBuilder();
        Random rnd = new Random();

        key.append("scroll-");

        while (key.length() < 16) { // length of the random string.
            int index = (int) (rnd.nextFloat() * CHARS.length());
            key.append(CHARS.charAt(index));
        }
        return key.toString();
    }

}
