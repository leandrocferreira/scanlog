package scanlog.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import scanlog.model.ReportModel;
import scanlog.model.TimestampModel;

/**
 * Main class that receives a log file as input and export a XML file as output.
 * @author Leandro Ferreira
 *
 */
public class Main {
	
	private static int duplicates = 0;
	private static int unnecessary = 0;

	public static void main(String[] args) {
		
		if (args.length == 0 || StringUtils.isBlank(args[0])) {
			System.out.println("Pass the full path of log file as argument.");
			System.out.println("Example: java -jar scanlog.jar C:\\server.log");
			return;
		}
		
		ReportModel reportModel = null;
		Map<ReportModel, TimestampModel> mapReport = new LinkedHashMap<>(); //Maps a document with its starts and gets
		Map<String, ReportModel> mapThreadWithoutRenderingUID = new HashMap<>(); //Maps a thread with its startRendering arguments
		
		
		try {			
			File myObj = new File(args[0]);
			Scanner myReader = new Scanner(myObj);
			
			while (myReader.hasNextLine()) {
				String data = myReader.nextLine();
				
				//Keep arguments passed (documentId, page) when startRendering is executed by a thread.
				if (data.contains("Executing request startRendering")) {
					reportModel = new ReportModel();
					reportModel.setDocumentId(StringUtils.substringBetween(data, "arguments [", "] on service").split(",")[0]);
					reportModel.setPage(StringUtils.substringBetween(data, "arguments [", "] on service").split(",")[1].trim());
					reportModel.setStartRenderingTimestamp(data.substring(0, 24));
					
					mapThreadWithoutRenderingUID.put(StringUtils.substringBetween(data, "[WorkerThread-", "]"), reportModel);
				}
				
				//Associate UID of startRendering to the correct thread.
				//Associate starts to respectives UIDs
				if (data.contains("Service startRendering returned")) {
					String threadNumber = StringUtils.substringBetween(data, "[WorkerThread-", "]");
					reportModel = mapThreadWithoutRenderingUID.get(threadNumber);					
					if (reportModel != null) {
						reportModel.setStartRenderingUID(StringUtils.substringAfter(data, "Service startRendering returned").trim());
						if (mapReport.get(reportModel) == null) {
							mapReport.put(reportModel, new TimestampModel());						
						}
						mapReport.get(reportModel).getStartRenderingList().add(reportModel.getStartRenderingTimestamp());
					}
					mapThreadWithoutRenderingUID.remove(threadNumber);
				}
				
				//Associate getRendering executed to respective UID.
				if (data.contains("Executing request getRendering")) {					
					ReportModel reportModelGetRendering = new ReportModel();
					reportModelGetRendering.setStartRenderingUID(StringUtils.substringBetween(data, "Executing request getRendering with arguments [", "]"));
					if (mapReport.get(reportModelGetRendering) != null) {
						mapReport.get(reportModelGetRendering).getRenderingList().add(data.substring(0, 24));
					}
				}
			}
			myReader.close();
			
		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
			return;
		}
		
		try {
			Element report = new Element("report");
			Document document = new Document(report);
			
			//create tag rendering for each different UID document. 
			mapReport.entrySet().forEach(r -> {							
				Element rendering = new Element("rendering");
				rendering.addContent(new Element("document").setText(r.getKey().getDocumentId()));
				rendering.addContent(new Element("page").setText(r.getKey().getPage()));
				rendering.addContent(new Element("uid").setText(r.getKey().getStartRenderingUID()));
				
				//starts of rendering
				r.getValue().getStartRenderingList().forEach(t -> {						
					rendering.addContent(new Element("start").setText(t));													
				});
				
				//Multiple starts with same UID
				if (r.getValue().getStartRenderingList().size() > 1) {
					duplicates++;
				}
				
				//gets of rendering
				r.getValue().getRenderingList().forEach(t -> {
					rendering.addContent(new Element("get").setText(t));							
				});
				
				//Number of startRenderings without get
				if (r.getValue().getRenderingList().size() == 0) {
					unnecessary++;
				}
				
				report.addContent(rendering);
			});
			
			//create tag summary
			Element summary= new Element("summary");
			summary.addContent(new Element("count").setText(mapReport.entrySet().size()+""));
			summary.addContent(new Element("duplicates").setText(duplicates+""));
			summary.addContent(new Element("unnecessary").setText(unnecessary+""));
			
			report.addContent(summary);
			
			XMLOutputter xmlOutput = new XMLOutputter();			
			xmlOutput.setFormat(Format.getPrettyFormat());
			xmlOutput.output(document, new FileWriter("output.xml"));
			
			System.out.println("XML generated successfully.");
			
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}
}

