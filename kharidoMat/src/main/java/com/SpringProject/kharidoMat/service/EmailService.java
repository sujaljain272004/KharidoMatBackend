package com.SpringProject.kharidoMat.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.SpringProject.kharidoMat.model.Booking;
import com.SpringProject.kharidoMat.model.User;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream; // Use the standard Java class
import java.time.YearMonth;

import jakarta.mail.internet.MimeMessage;

import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Element;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPCell;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private TemplateEngine templateEngine;

    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("your_email@gmail.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            logger.info("Email sent successfully to {}", to);
        } catch (Exception e) {
            logger.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }

    public void sendOtpEmail(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("KharidoMat Return OTP");
            message.setText("Your OTP for returning the item is: " + otp + "\nValid for 10 minutes.");
            mailSender.send(message);
            logger.info("OTP email sent successfully to {}", toEmail);
        } catch (Exception e) {
            logger.error("Failed to send OTP email to {}: {}", toEmail, e.getMessage());
        }
    }
    
    @Async // Makes email sending non-blocking
    public void sendBookingConfirmationEmail(Booking booking) {
        try {
            // Prepare the Thymeleaf context
            Context context = new Context();
            context.setVariable("booking", booking);
            String htmlContent = templateEngine.process("booking-confirmation", context);

            // Create the email message
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo(booking.getUser().getEmail());
            helper.setSubject("Your Booking Confirmation for " + booking.getItem().getTitle());
            helper.setText(htmlContent, true);

            // Generate and attach the PDF invoice
            byte[] pdfInvoice = generateInvoicePdf(booking);
            helper.addAttachment("invoice.pdf", new ByteArrayResource(pdfInvoice));

            mailSender.send(mimeMessage);
            logger.info("Successfully sent booking confirmation email to {}", booking.getUser().getEmail());

        } catch (Exception e) {
            logger.error("Failed to send booking confirmation email for booking ID {}:", booking.getId(), e);
            e.printStackTrace();
        }
    }
    
    private byte[] generateInvoicePdf(Booking booking) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);

        document.open();

        // --- 1. Define Fonts ---
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

        // --- 2. Add Header ---
        Paragraph title = new Paragraph("Campus Rental (kharidomat)", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Paragraph subTitle = new Paragraph("Booking Invoice", headerFont);
        subTitle.setAlignment(Element.ALIGN_CENTER);
        subTitle.setSpacingAfter(20f); // Add space after this element
        document.add(subTitle);

        // --- 3. Create a Table for Details ---
        PdfPTable table = new PdfPTable(2); // 2 columns
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1f, 2f}); // Column 1 is 1 part, Column 2 is 2 parts wide

        // Add rows to the table
        addInvoiceRow(table, "Invoice #:", "INV-" + booking.getId(), boldFont, normalFont);
        addInvoiceRow(table, "Date:", booking.getStartDate().toString(), boldFont, normalFont);
        addInvoiceRow(table, "Booked By:", booking.getUser().getFullName(), boldFont, normalFont);
        addInvoiceRow(table, "Item:", booking.getItem().getTitle(), boldFont, normalFont);
        addInvoiceRow(table, "Rental Period:", booking.getStartDate().toString() + " to " + booking.getEndDate().toString(), boldFont, normalFont);
        addInvoiceRow(table, "Status:", booking.getStatus().toString(), boldFont, normalFont);
        addInvoiceRow(table, "Total Amount:", "Rs. " + String.format("%.2f", booking.getAmount()), boldFont, boldFont); // Make amount bold
        
        addInvoiceRow(table, "Payment ID:", booking.getRazorpayPaymentId(), boldFont, normalFont);
        addInvoiceRow(table, "Order ID:", booking.getRazorpayOrderId(), boldFont, normalFont);

        document.add(table);

        // --- 4. Add Footer ---
        Paragraph footer = new Paragraph("Thank you for your business!", normalFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(30f);
        document.add(footer);

        document.close();
        return outputStream.toByteArray();
    }

    // --- Add this private helper method to your EmailService class ---
    private void addInvoiceRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        // Label Cell
        PdfPCell labelCell = new PdfPCell(new Paragraph(label, labelFont));
        labelCell.setBorder(PdfPCell.NO_BORDER);
        labelCell.setPadding(5);
        table.addCell(labelCell);

        // Value Cell
        PdfPCell valueCell = new PdfPCell(new Paragraph(value, valueFont));
        valueCell.setBorder(PdfPCell.NO_BORDER);
        valueCell.setPadding(5);
        table.addCell(valueCell);
    }
    
 // In your EmailService.java

    @Async
    public void sendOwnerNotificationEmail(Booking booking) {
        logger.info("Attempting to send new booking notification to owner {}", booking.getItem().getUser().getEmail());
        try {
            Context context = new Context();
            context.setVariable("booking", booking);
            String htmlContent = templateEngine.process("owner-notification", context);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo(booking.getItem().getUser().getEmail());
            helper.setSubject("New Booking for your item: " + booking.getItem().getTitle());
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            logger.info("Successfully sent new booking notification to owner {}", booking.getItem().getUser().getEmail());

        } catch (Exception e) {
            logger.error("Failed to send owner notification for booking ID {}:", booking.getId(), e);
        }
    }
    
 // In EmailService.java

    @Async
    public void sendMonthlyReportEmail(User owner, YearMonth month, byte[] excelFile) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo(owner.getEmail());
            helper.setSubject("Your Campus Rental Monthly Report for " + month.toString());
            helper.setText("Hello " + owner.getFullName() + ",\n\nPlease find your monthly performance report attached.\n\nThank you,\nThe Campus Rental Team");

            String fileName = "Report-" + month.toString() + ".xlsx";
            helper.addAttachment(fileName, new ByteArrayResource(excelFile));

            mailSender.send(mimeMessage);
            logger.info("Successfully sent monthly report to {}", owner.getEmail());

        } catch (Exception e) {
            logger.error("Failed to send monthly report to {}:", owner.getEmail(), e);
        }
    }
}
