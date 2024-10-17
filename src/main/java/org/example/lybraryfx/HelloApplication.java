package org.example.lybraryfx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;

public class HelloApplication extends Application {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/Lybrary";
    private static final String USER = "root";
    private static final String PASSWORD = "111000";

    private ListView<String> bookList;
    private TextField titleField, authorField, yearField, idField;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        bookList = new ListView<>();
        bookList.setStyle("-fx-font-size: 16px;" +
                "-fx-font-family:Arial");
        titleField = new TextField();
        authorField = new TextField();
        yearField = new TextField();
        idField = new TextField();

        Button addButton = new Button("Добавить книгу");
        Button updateButton = new Button("Обновить книгу");
        Button deleteButton = new Button("Удалить книгу");
        Button viewButton = new Button("Показать все книги");

        addButton.setOnAction(e -> addBook());
        updateButton.setOnAction(e -> updateBook());
        deleteButton.setOnAction(e -> deleteBook());
        viewButton.setOnAction(e -> viewBooks());

        HBox buttonLayout = new HBox(15, addButton, updateButton, deleteButton, viewButton);
        buttonLayout.setStyle("-fx-alignment: center;"); // Центрирование

        VBox layout = new VBox(20, new Label("ID:"), idField, new Label("Название:"), titleField,
                new Label("Автор:"), authorField, new Label("Год:"), yearField,
                buttonLayout, bookList);

        layout.setStyle("-fx-background-color: #ffedcf;" +
                "-fx-font-size: 16px;" +
                "-fx-alignment: center;" +
                "-fx-padding:15px");

        Scene scene = new Scene(layout, 1000, 600);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(550);
        primaryStage.setTitle("Library Management");
        primaryStage.show();
    }

    private void addBook() {
        int id = Integer.parseInt(idField.getText());
        String title = titleField.getText();
        String author = authorField.getText();
        int year = Integer.parseInt(yearField.getText());
        String query = "INSERT INTO books (id,title, author, year) VALUES (?,?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, title);
            preparedStatement.setString(3, author);
            preparedStatement.setInt(4, year);
            preparedStatement.executeUpdate();
            System.out.println("Книга добавлена: " + title);
            viewBooks(); // Обновить список книг

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void viewBooks() {
        bookList.getItems().clear();
        String query = "SELECT * FROM books";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String bookInfo = "ID: " + resultSet.getInt("id") + ", Title: " + resultSet.getString("title") +
                        ", Author: " + resultSet.getString("author") + ", Year: " + resultSet.getInt("year");
                bookList.getItems().add(bookInfo);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateBook() {
        int id = Integer.parseInt(idField.getText());
        String title = titleField.getText();
        String author = authorField.getText();
        int year = Integer.parseInt(yearField.getText());
        String query = "UPDATE books SET title = ?, author = ?, year = ? WHERE id = ?";

        // Если поле id пустое
        if (title.isEmpty() || author.isEmpty() || yearField.getText().isEmpty()) {
            System.out.println("Пожалуйста, заполните все поля!");
            return;
        }

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, author);
            preparedStatement.setInt(3, year);
            preparedStatement.setInt(4, id);
            if (preparedStatement.executeUpdate() > 0) {
                System.out.println("Книга обновлена с ID: " + id);
                viewBooks(); // Обновить список книг
            } else {
                System.out.println("Книга с указанным ID не найдена.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteBook() {
        int id = Integer.parseInt(idField.getText());
        String query = "DELETE FROM books WHERE id = ?";

        // Если поле id пустое
        if (idField.getText().isEmpty()) {
            System.out.println("Пожалуйста, укажите ID книги для удаления!");
            return;
        }

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            if (preparedStatement.executeUpdate() > 0) {
                System.out.println("Книга удалена с ID: " + id);
                viewBooks(); // Обновить список книг
            } else {
                System.out.println("Книга с указанным ID не найдена.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
