package com.revpay.dao;

import com.revpay.model.BusinessProfile;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class BusinessProfileDao {

    public void insert(BusinessProfile bp, Connection con) throws Exception {

        String sql = """
            INSERT INTO business_profiles (user_id, business_type, tax_id, address)
            VALUES (?, ?, ?, ?)
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, bp.getUserId());
            ps.setString(2, bp.getBusinessType());
            ps.setString(3, bp.getTaxId());
            ps.setString(4, bp.getAddress());
            ps.executeUpdate();
        }
    }
}
