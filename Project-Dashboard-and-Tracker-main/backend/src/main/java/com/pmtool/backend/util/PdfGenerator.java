package com.pmtool.backend.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.pmtool.backend.DTO.SalarySlipDto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PdfGenerator {

	public static ByteArrayInputStream generateSlip(SalarySlipDto slip) {
		double gross = slip.getBasic() + slip.getHra() + slip.getSpecial();
		double totalDeductions = slip.getProfessionalTax() + slip.getTds() + slip.getProvidentFund();
		double net = gross - totalDeductions;

		Document document = new Document(PageSize.A5, 20, 20, 15, 15);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			PdfWriter.getInstance(document, out);
			document.open();

			Image logo = Image.getInstance(PdfGenerator.class.getClassLoader().getResource("logo192.png"));
			logo.scaleAbsolute(60, 60);
			logo.setAlignment(Image.ALIGN_LEFT);
			document.add(logo);

			Font companyFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
			Font headerFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
			Font normalFont = new Font(Font.FontFamily.HELVETICA, 10);
			Font boldFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);

			Paragraph company = new Paragraph("Fist Engineering Solutions LLP", companyFont);
			company.setAlignment(Element.ALIGN_LEFT);
			document.add(company);

			Paragraph address = new Paragraph("First Floor Mak Mall Kankanady,\nMangalore-575002 Karnataka, India.",
					normalFont);
			address.setAlignment(Element.ALIGN_LEFT);
			document.add(address);

			document.add(Chunk.NEWLINE);

			Paragraph title = new Paragraph("Employee Pay Summary", headerFont);
			title.setAlignment(Element.ALIGN_LEFT);
			title.setSpacingAfter(5);
			document.add(title);

			PdfPTable empTable = new PdfPTable(4);
			empTable.setWidthPercentage(100);
			empTable.setSpacingBefore(0);
			empTable.setWidths(new float[] { 3, 3, 3, 3 });

			addLabelValue(empTable, "Employee Name", slip.getEmployeeName(), "Employee ID", slip.getEmployeeId(),
					normalFont);
			addLabelValue(empTable, "Joining Date", slip.getJoiningDate().toString(), "Pay Period", slip.getMonth(),
					normalFont);
			addLabelValue(empTable, "Paid Days", String.valueOf(slip.getTotalDays()), "Loss of Pay Days",
					String.valueOf(slip.getLopDays()), normalFont);

			PdfPTable wrapper = new PdfPTable(1);
			wrapper.setWidthPercentage(100);

			PdfPCell wrapperCell = new PdfPCell(empTable);
			wrapperCell.setBorder(Rectangle.BOX);
			wrapperCell.setBorderWidthRight(0);
			wrapperCell.setBorderWidthLeft(0);
			wrapperCell.setPaddingBottom(0);

			wrapper.addCell(wrapperCell);
			document.add(wrapper);

//			document.add(empTable);

			document.add(Chunk.NEWLINE);

			Paragraph salDetailsTitle = new Paragraph("Salary Details :", headerFont);
			salDetailsTitle.setAlignment(Element.ALIGN_LEFT);
			salDetailsTitle.setSpacingAfter(5);
			document.add(salDetailsTitle);

			PdfPTable salaryTable = new PdfPTable(4);
			salaryTable.setWidthPercentage(100);
			salaryTable.setWidths(new float[] { 3, 2, 3, 2 });

			addHeader(salaryTable, "Earnings");
			addHeader(salaryTable, "Amount");
			addHeader(salaryTable, "Deductions");
			addHeader(salaryTable, "Amount");

			String earnings = "Basic + D.A House\n\nRent Allowance\n\nSpecial Allowance\n\nEmployer ESI @ 3.25%";
			String earningAmounts = (gross <= 21000)
					? String.format("%.2f\n\n%.2f\n\n%.2f\n\n%.2f", slip.getBasic(), slip.getHra(), slip.getSpecial(),
							slip.getEmployerEsi())
					: String.format("%.2f\n\n%.2f\n\n%.2f\n\nN/A", slip.getBasic(), slip.getHra(), slip.getSpecial());

			String deductions = "Income Tax\n\nProfessional Tax ESI\n\nESI\n\nEmployee ESI @ 0.75%\n\nEmployer ESI @ 3.25%\n\nLoss of Pay (LOP)";
			String deductionAmounts = (gross <= 21000)
					? String.format("%.2f\n\n%.2f\n\n%.2f\n\n%.2f\n\n\n%.2f\n\n\n%.2f", slip.getTds(),
							slip.getProfessionalTax(), slip.getEsi(), slip.getEmployeeEsi(), slip.getEmployerEsi(),
							slip.getLopAmount())
					: String.format("%.2f\n\n%.2f\n\nN/A\n\nN/A\n\n\nN/A\n\n\n%.2f", slip.getTds(),
							slip.getProfessionalTax(), slip.getLopAmount());

			Font font = new Font(Font.FontFamily.HELVETICA, 10);

			addRow(salaryTable, earnings, font);
			addRow(salaryTable, earningAmounts, font);
			addRow(salaryTable, deductions, font);
			addRow(salaryTable, deductionAmounts, font);

			addRow(salaryTable, "Gross Earnings", font);
			addRow(salaryTable, String.valueOf(gross), font);
			addRow(salaryTable, "Total Deduction", font);
			addRow(salaryTable, String.valueOf(totalDeductions), font);

			Font footerFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);

			addRow(salaryTable, "", footerFont);
			addRow(salaryTable, "", footerFont);
			addRow(salaryTable, "Net Amount", footerFont);
			addRow(salaryTable, String.valueOf(net), footerFont);
			document.add(salaryTable);

			document.add(Chunk.NEWLINE);

			Paragraph words = new Paragraph(NumberToWords.convert(net), boldFont);
			words.setSpacingBefore(5);
			document.add(words);
			Paragraph comGenWord = new Paragraph("*THIS IS COMPUTER GENERATED SALARY SLIP", font);
			comGenWord.setSpacingBefore(50);
			comGenWord.setAlignment(Element.ALIGN_CENTER);
			document.add(comGenWord);
			document.close();
		} catch (DocumentException | IOException e) {
			e.printStackTrace();
		}
		return new ByteArrayInputStream(out.toByteArray());

	}

	private static void addLabelValue(PdfPTable table, String label1, String value1, String label2, String value2,
			Font font) {
		PdfPCell cell1 = new PdfPCell(new Phrase(label1 + " :", font));
		cell1.setBorder(Rectangle.NO_BORDER);
		cell1.setPadding(5);
		table.addCell(cell1);

		PdfPCell cell2 = new PdfPCell(new Phrase(value1 != null ? value1 : "", font));
		cell2.setBorder(Rectangle.NO_BORDER);
		cell2.setPadding(5);
		table.addCell(cell2);

		PdfPCell cell3 = new PdfPCell(new Phrase(label2 + " :", font));
		cell3.setBorder(Rectangle.NO_BORDER);
		cell3.setPadding(5);
		table.addCell(cell3);

		PdfPCell cell4 = new PdfPCell(new Phrase(value2 != null ? value2 : "", font));
		cell4.setBorder(Rectangle.NO_BORDER);
		cell4.setPadding(5);
		table.addCell(cell4);
	}

	private static void addRow(PdfPTable table, String data, Font font) {
		PdfPCell cell = new PdfPCell(new Phrase(data, font));
		cell.setPadding(5);
		table.addCell(cell);
	}

	private static void addHeader(PdfPTable table, String text) {
		Font headerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.BLACK);

		PdfPCell header = new PdfPCell(new Phrase(text, headerFont));
//		header.setBackgroundColor(new BaseColor(0, 102, 204)); // Blue
		header.setHorizontalAlignment(Element.ALIGN_CENTER);
		header.setPadding(6);

		table.addCell(header);
	}

}
