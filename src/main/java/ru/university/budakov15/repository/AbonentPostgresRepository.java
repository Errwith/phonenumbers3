package ru.university.budakov15.repository;

import ru.university.budakov15.exception.BusinessException;
import ru.university.budakov15.model.Abonent;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AbonentPostgresRepository implements AbonentRepository {
    private final String url;
    private final String user;
    private final String password;

    private static final String ORDER_BY_CLAUSE =
            " ORDER BY LOWER(last_name) ASC, LOWER(first_name) ASC, " +
                    "NULLIF(LOWER(phone1), '') ASC NULLS LAST, " +
                    "NULLIF(LOWER(phone2), '') ASC NULLS LAST, " +
                    "NULLIF(LOWER(phone3), '') ASC NULLS LAST";

    public AbonentPostgresRepository(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
        initTable();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    private void initTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS abonents (
                    id SERIAL PRIMARY KEY,
                    last_name VARCHAR(50) NOT NULL,
                    first_name VARCHAR(50) NOT NULL,
                    phone1 VARCHAR(30) NOT NULL DEFAULT '',
                    phone2 VARCHAR(30) NOT NULL DEFAULT '',
                    phone3 VARCHAR(30) NOT NULL DEFAULT ''
                );
                """;
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new BusinessException("Ошибка инициализации БД PostgreSQL: " + e.getMessage());
        }
    }

    @Override
    public Abonent save(Abonent abonent) {
        String sql = "INSERT INTO abonents (last_name, first_name, phone1, phone2, phone3) VALUES (?, ?, ?, ?, ?) RETURNING id";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, abonent.getLastName());
            ps.setString(2, abonent.getFirstName());
            ps.setString(3, abonent.getPhone1());
            ps.setString(4, abonent.getPhone2());
            ps.setString(5, abonent.getPhone3());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    abonent.setId(rs.getInt("id"));
                }
            }
            return abonent;
        } catch (SQLException e) {
            throw new BusinessException("Ошибка при сохранении абонента в БД: " + e.getMessage());
        }
    }

    @Override
    public void update(Abonent abonent) {
        String sql = "UPDATE abonents SET last_name = ?, first_name = ?, phone1 = ?, phone2 = ?, phone3 = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, abonent.getLastName());
            ps.setString(2, abonent.getFirstName());
            ps.setString(3, abonent.getPhone1());
            ps.setString(4, abonent.getPhone2());
            ps.setString(5, abonent.getPhone3());
            ps.setInt(6, abonent.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BusinessException("Ошибка при обновлении абонента в БД: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteById(int id) {
        String sql = "DELETE FROM abonents WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new BusinessException("Ошибка при удалении абонента из БД: " + e.getMessage());
        }
    }

    @Override
    public Optional<Abonent> findById(int id) {
        String sql = "SELECT id, last_name, first_name, phone1, phone2, phone3 FROM abonents WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new BusinessException("Ошибка при поиске абонента по ID в БД: " + e.getMessage());
        }
    }

    @Override
    public List<Abonent> findAll() {
        String sql = "SELECT id, last_name, first_name, phone1, phone2, phone3 FROM abonents" + ORDER_BY_CLAUSE;
        List<Abonent> result = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(mapRow(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new BusinessException("Ошибка при получении списка абонентов из БД: " + e.getMessage());
        }
    }

    @Override
    public List<Abonent> findByLastName(String lastName) {
        String sql = "SELECT id, last_name, first_name, phone1, phone2, phone3 FROM abonents " +
                "WHERE last_name ILIKE ?" + ORDER_BY_CLAUSE;
        List<Abonent> result = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + lastName.trim() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
            return result;
        } catch (SQLException e) {
            throw new BusinessException("Ошибка при поиске по фамилии в БД: " + e.getMessage());
        }
    }

    @Override
    public List<Abonent> findByPhone(String phone) {
        String sql = "SELECT id, last_name, first_name, phone1, phone2, phone3 FROM abonents " +
                "WHERE (phone1 <> '' AND phone1 ILIKE ?) " +
                "   OR (phone2 <> '' AND phone2 ILIKE ?) " +
                "   OR (phone3 <> '' AND phone3 ILIKE ?)" + ORDER_BY_CLAUSE;
        List<Abonent> result = new ArrayList<>();
        String pattern = "%" + phone.trim() + "%";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
            return result;
        } catch (SQLException e) {
            throw new BusinessException("Ошибка при поиске по телефону в БД: " + e.getMessage());
        }
    }

    private Abonent mapRow(ResultSet rs) throws SQLException {
        return new Abonent(
                rs.getInt("id"),
                rs.getString("last_name"),
                rs.getString("first_name"),
                rs.getString("phone1"),
                rs.getString("phone2"),
                rs.getString("phone3")
        );
    }
}