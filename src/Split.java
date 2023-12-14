import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.Row;

import java.io.File; 

public class Split {

	
	
	public static void main(String[] args) throws Exception {
		boolean alloted=false, profit =false;
		Double profit_amount = 0.00, loss_amount=0.00;
		
		List<Investor> investorList= new ArrayList<Investor>();
		double tax_rate=0.00;
		
		Scanner sc= new Scanner(System.in);
		System.out.println("Enter the IPO Name:");
		String ipoName= sc.nextLine();
		
		System.out.println("Enter Total Amount");
		Double total_amount=sc.nextDouble();
		
		System.out.println("Enter ApplicationNo");
		String application_no=sc.next();
		
		System.out.println("Enter No of Investors");
		int numberof_Investors=sc.nextInt();
		
		
		for(int i=0;i<numberof_Investors;i++) {
			System.out.println("Enter Investor "+ (i+1)+" deatils: ");
			System.out.println("Enter Name");
			String name= sc.next();
			System.out.println("Enter Amount");
			Double amount= sc.nextDouble();
			investorList.add(new Investor(name,amount));
		}
		
		Double members_total_amount=investorList.stream().map(p->p.getAmount()).reduce(0D,(a,b)->a+b);

		if(!total_amount.equals(members_total_amount)){
        	throw new Exception("Memebers total investment not matching total amount");
        }
		
		System.out.println("IS Alloted:(Y/N)");
		String isAlloted=sc.next();
		alloted=isAlloted.equalsIgnoreCase("y")?true:false;
		if(alloted) {
			System.out.println("Profit/Loss:(P/L)");
			String isProfit=sc.next();
			profit=isProfit.equalsIgnoreCase("p")?true:(isProfit.equalsIgnoreCase("l")?false: null);
			if(profit) {
				tax_rate=15;
				System.out.println("Enter Profit amount:(Amount got-Invested amount)");
				profit_amount=sc.nextDouble();
			}else {
				System.out.println("Enter Loss amount:(Invested amount-Amount got)");
				loss_amount=sc.nextDouble();
			}
		}
		
	    PDDocument doc = new PDDocument();
	    PDDocumentInformation pdd = doc.getDocumentInformation();  
	    pdd.setTitle(ipoName);  
	    pdd.setSubject(ipoName);  
	    
	    PDPage page = new PDPage(PDRectangle.A4);  
	    doc.addPage(page);  
	      
	    PDPageContentStream contentStream = new PDPageContentStream(doc, page);  
	      
	    contentStream.beginText();  
	    contentStream.setFont(PDType1Font.COURIER_BOLD, 18);  
	    contentStream.newLineAtOffset(150, 750);  
	    contentStream.showText("IPO Details : "+ipoName);  
	    contentStream.endText();  
	    
	    contentStream.beginText();
	    contentStream.setLeading(14.5f);
	    contentStream.setFont(PDType1Font.HELVETICA, 12);
	    contentStream.newLineAtOffset(35, 700);
	    contentStream.showText("IPO Name:  "+ipoName); 
	    contentStream.newLine();
	    contentStream.showText("Amount:    "+total_amount); 
	    contentStream.newLine(); 
	    contentStream.showText("Application No: "+application_no); 
	    contentStream.endText();
	    
	    contentStream.beginText();
        float yTableStart = PDRectangle.A4.getHeight() - 220;
        float tableWidth = PDRectangle.A4.getWidth() - 150;
        BaseTable table = new BaseTable(yTableStart, yTableStart, 220, tableWidth, 20, doc, page, true, true);
        Row<PDPage> hRow = table.createRow(20f);
        Cell<PDPage> cell = null;
        hRow.createCell(8, "SI No").setFont(PDType1Font.HELVETICA);
        hRow.createCell(18, "Name").setFont(PDType1Font.HELVETICA);
        hRow.createCell(16, "Amount Invested").setFont(PDType1Font.HELVETICA);
        hRow.createCell(20, "Percentage Invested").setFont(PDType1Font.HELVETICA);
        hRow.createCell(20, "Profit/Loss").setFont(PDType1Font.HELVETICA);
        hRow.createCell(10, "Tax Amount").setFont(PDType1Font.HELVETICA);
        hRow.createCell(20, "Amount Receivable").setFont(PDType1Font.HELVETICA);
        
        table.addHeaderRow(hRow);
        int count=1;
        double d=1.00/100.00;
        for(Investor investor :investorList){
        	double pl_amount = 0.00,tax_amount=0.00;
        	double receivable_amount=investor.getAmount();
        	double percentage_invested=(investor.getAmount()*100)/total_amount;
        	String pl_symbol="";
        	
        	if(alloted && profit) {
        		pl_amount= percentage_invested*d*profit_amount;	
        		tax_amount = tax_rate*d*pl_amount;
        		receivable_amount=pl_amount-tax_amount;
        		pl_symbol="+";
        	}else if(alloted && !profit) {
        		double recovered_amount=total_amount-loss_amount;
        		pl_amount= percentage_invested*d*recovered_amount;	
        		tax_amount = tax_rate*d*pl_amount;
        		receivable_amount=pl_amount-tax_amount;
        		pl_symbol="-";
        	}
        			
            Row<PDPage> row = table.createRow(27);
            cell = row.createCell(8, String.valueOf(count));
            cell = row.createCell(18,investor.getName());
            cell = row.createCell(16, String.valueOf(investor.getAmount()));
            cell = row.createCell(20, String.valueOf(percentage_invested)+" %");
            cell = row.createCell(20, pl_symbol+String.valueOf(pl_amount));
            cell = row.createCell(10, String.valueOf(tax_amount));
            cell = row.createCell(20, String.valueOf(receivable_amount));
            count++;
        }
        table.draw();
        contentStream.endText();  
	    
 
	    contentStream.close();
	    String home = System.getProperty("user.home");
	    doc.save(new File(home+"/Downloads/"+ipoName+".pdf"));
	    doc.close(); 
	}
	
}
