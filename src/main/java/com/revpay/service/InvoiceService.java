package com.revpay.service;

import com.revpay.db.InvoiceDao;
import com.revpay.db.UserDao;
import com.revpay.model.Invoice;
import com.revpay.model.User;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;
public class InvoiceService {

    private final InvoiceDao invoiceDao = new InvoiceDao();
    private final UserDao userDao = new UserDao();
    private final MoneyTransferService transferService = new MoneyTransferService();

    // BUSINESS ACTION
    public long createInvoice(
            long businessUserId,
            long customerUserId,
            BigDecimal amount,
            LocalDate dueDate,
            Connection con
    ) throws Exception {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be positive");
        }

        User customer = userDao.findById(customerUserId, con);
        if (customer == null) {
            throw new RuntimeException("Customer not found");
        }

        if (!"PERSONAL".equalsIgnoreCase(customer.getUserType())) {
            throw new RuntimeException("Invoices can be sent only to PERSONAL users");
        }

        Invoice invoice = new Invoice();
        invoice.setBusinessUserId(businessUserId);
        invoice.setCustomerUserId(customerUserId);
        invoice.setTotalAmount(amount);
        invoice.setStatus("UNPAID");
        invoice.setDueDate(dueDate);

        return invoiceDao.create(invoice, con);
    }

    // PERSONAL ACTION
    public void payInvoice(
            long invoiceId,
            long payerUserId,
            Connection con
    ) throws Exception {

        Invoice invoice = invoiceDao.findById(invoiceId, con);

        if (invoice == null)
            throw new RuntimeException("Invoice not found");

        if (!"UNPAID".equals(invoice.getStatus()))
            throw new RuntimeException("Invoice already paid");

        if (invoice.getCustomerUserId() != payerUserId)
            throw new RuntimeException("This invoice does not belong to you");

        transferService.transferMoneyInternal(
                payerUserId,
                invoice.getBusinessUserId(),
                invoice.getTotalAmount(),
                "Invoice payment #" + invoiceId,
                con
        );

        invoiceDao.markPaid(invoiceId, con);
    }
}
