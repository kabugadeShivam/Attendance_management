import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

class Student {
    private String name;
    private int id;

    public Student(String name, int id) {
        this.name = name;
        this.id = id; 
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }
}

class AttendanceManager {
    private ArrayList<Student> students;
    private HashMap<Integer, ArrayList<Boolean>> attendanceRecords;
    private String adminUsername;
    private String adminPassword;

    public AttendanceManager() {
        students = new ArrayList<>();
        attendanceRecords = new HashMap<>();
        adminUsername = "mgm";      
        adminPassword = "1234";    
    }

    public void addStudent(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Student name cannot be empty");
        }
        int id = students.size() + 1;
        students.add(new Student(name, id));
        attendanceRecords.put(id, new ArrayList<>());
        JOptionPane.showMessageDialog(null, "Added student: " + name + " with ID: " + id);
    }

    public void markAttendance(int studentId, boolean present) {
        if (!attendanceRecords.containsKey(studentId)) {
            throw new IllegalArgumentException("Student ID not found.");
        }
        attendanceRecords.get(studentId).add(present);
        JOptionPane.showMessageDialog(null, "Marked attendance for student ID: " + studentId + " as " + (present ? "Present" : "Absent"));
    }

    public String[][] getAttendanceData() {
        String[][] data = new String[students.size()][2];
        for (int i = 0; i < students.size(); i++) {
            Student student = students.get(i);
            data[i][0] = student.getName();
            ArrayList<Boolean> attendance = attendanceRecords.get(student.getId());
            int presentCount = 0;
            for (Boolean present : attendance) {
                if (present) presentCount++;
            }
            double attendancePercentage = attendance.size() > 0 ? (double) presentCount / attendance.size() * 100 : 0;
            data[i][1] = String.format("%.2f%%", attendancePercentage);
        }
        return data;
    }

    public String viewAttendance() {
        StringBuilder attendanceRecordsStr = new StringBuilder("Attendance Records:\n");
        for (Student student : students) {
            attendanceRecordsStr.append("ID: ").append(student.getId()).append(" (").append(student.getName()).append("): ");
            ArrayList<Boolean> attendance = attendanceRecords.get(student.getId());
            for (Boolean present : attendance) {
                attendanceRecordsStr.append(present ? "P " : "A ");
            }
            attendanceRecordsStr.append("\n");
        }
        return attendanceRecordsStr.toString();
    }

    public boolean authenticateAdmin(String username, String password) {
        return username.equals(adminUsername) && password.equals(adminPassword);
    }
}

public class AttendanceApp {
    private AttendanceManager attendanceManager;

    public AttendanceApp() {
        attendanceManager = new AttendanceManager();
        createLoginFrame();
    }

    private void createLoginFrame() {
        JFrame loginFrame = new JFrame("Login");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(300, 150);
        loginFrame.setLayout(new FlowLayout());

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(15);
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(15);
        JButton loginButton = new JButton("Login");

        loginFrame.add(usernameLabel);
        loginFrame.add(usernameField);
        loginFrame.add(passwordLabel);
        loginFrame.add(passwordField);
        loginFrame.add(loginButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (attendanceManager.authenticateAdmin(username, password)) {
                    JOptionPane.showMessageDialog(loginFrame, "Login successful.");
                    loginFrame.dispose();
                    createMainFrame();
                } else {
                    JOptionPane.showMessageDialog(loginFrame, "Invalid credentials.");
                }
            }
        });

        loginFrame.setVisible(true);
    }

    private void createMainFrame() {
        JFrame mainFrame = new JFrame("Attendance Management");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(400, 300);
        mainFrame.setLayout(new GridLayout(5, 1)); // Adjusted grid size

        JButton addStudentButton = new JButton("Add Student");
        JButton markAttendanceButton = new JButton("Mark Attendance");
        JButton viewAttendanceButton = new JButton("View Attendance");
        JButton viewStatisticsButton = new JButton("View Attendance Statistics");
        JButton logoutButton = new JButton("Logout");

        mainFrame.add(addStudentButton);
        mainFrame.add(markAttendanceButton);
        mainFrame.add(viewAttendanceButton);
        mainFrame.add(viewStatisticsButton);
        mainFrame.add(logoutButton);

        addStudentButton.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(mainFrame, "Enter student name:");
            if (name != null && !name.trim().isEmpty()) {
                attendanceManager.addStudent(name);
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Invalid student name.");
            }
        });

        markAttendanceButton.addActionListener(e -> {
            String idStr = JOptionPane.showInputDialog(mainFrame, "Enter student ID:");
            if (idStr != null) {
                try {
                    int studentId = Integer.parseInt(idStr);
                    String presentStr = JOptionPane.showInputDialog(mainFrame, "Is present? (true/false):");
                    if (presentStr != null) {
                        boolean present = Boolean.parseBoolean(presentStr);
                        attendanceManager.markAttendance(studentId, present);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(mainFrame, "Invalid student ID.");
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(mainFrame, ex.getMessage());
                }
            }
        });

        viewAttendanceButton.addActionListener(e -> {
            String attendanceRecords = attendanceManager.viewAttendance();
            JOptionPane.showMessageDialog(mainFrame, attendanceRecords);
        });

        viewStatisticsButton.addActionListener(e -> {
            String[][] data = attendanceManager.getAttendanceData();
            String[] columnNames = {"Student Name", "Attendance Percentage"};
            JTable table = new JTable(data, columnNames);
            table.setFillsViewportHeight(true);
            JScrollPane scrollPane = new JScrollPane(table);
            JOptionPane.showMessageDialog(mainFrame, scrollPane, "Attendance Statistics", JOptionPane.INFORMATION_MESSAGE);
        });

        logoutButton.addActionListener(e -> {
            mainFrame.dispose();
            createLoginFrame();
        });

        mainFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AttendanceApp());
    }
}

