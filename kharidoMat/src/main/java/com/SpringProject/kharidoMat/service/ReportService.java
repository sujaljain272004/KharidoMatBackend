// In your service package
package com.SpringProject.kharidoMat.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SpringProject.kharidoMat.model.Booking;
import com.SpringProject.kharidoMat.model.Item;
import com.SpringProject.kharidoMat.model.User;
import com.SpringProject.kharidoMat.repository.BookingRepository;
import com.SpringProject.kharidoMat.repository.ItemRepository;

import java.io.ByteArrayOutputStream;
import java.time.YearMonth;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    public byte[] generateMonthlyReportForOwner(User owner, YearMonth month) throws Exception {
        List<Item> items = itemRepository.findByUser(owner);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(month.toString() + " Report");

        // Create Header Row
        String[] headers = {"Item Title", "Bookings This Month", "Total Days Rented", "Earnings This Month (Rs.)"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // Populate Data Rows
        int rowNum = 1;
        for (Item item : items) {
            List<Booking> bookings = bookingRepository.findCompletedBookingsForItemInMonth(item.getId(), month.getYear(), month.getMonthValue());
            
            if (!bookings.isEmpty()) {
                long totalDays = bookings.stream().mapToLong(b -> java.time.temporal.ChronoUnit.DAYS.between(b.getStartDate(), b.getEndDate()) + 1).sum();
                double totalEarnings = bookings.stream().mapToDouble(Booking::getAmount).sum();

                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(item.getTitle());
                row.createCell(1).setCellValue(bookings.size());
                row.createCell(2).setCellValue(totalDays);
                row.createCell(3).setCellValue(totalEarnings);
            }
        }
        
        // Write to byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }
}