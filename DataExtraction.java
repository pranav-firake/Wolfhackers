import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class HelloWorld {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		 String path = "C:/Users/pothugax/Downloads/corpus/citations_class/";
		 File file = new File(path);
		 HashMap<String,HashSet<Citation>> hash = new HashMap<>();
		 DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		 HashMap<String,String> fileMap = generateNodeFile(path,dbFactory);
		 processData(path,dbFactory,hash);
		 writeToCSV(hash,fileMap);
//		 System.out.println(hash.size());
//		 for(String s:hash.keySet()) {
//			 for(Citation c: hash.get(s)) {
//				 System.out.println(s + " " + c.citation_type + " " + c.file_name);
//			 }
//		 }
	}
	
	static String getFileName(String url) {
		String tokens[] = url.split("/");
		int year = Integer.parseInt(tokens[7]); 
		if(year >= 2006 && year <= 2009) {
			String fnum = tokens[8].split(".html")[0]; 
			return tokens[7].substring(2)+"_"+fnum;
		}
		return null;
	}
	
	static void processData(String dir, DocumentBuilderFactory dbFactory, HashMap<String,HashSet<Citation>> hash) {
		File folder = new File(dir);
		File files[] = folder.listFiles();
		for(File f:files) {
			String curfile = f.getName().split(".xml")[0];
			try {
				 dbFactory = DocumentBuilderFactory.newInstance();
				 DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				 Document doc = dBuilder.parse(f);
				 NodeList citations = doc.getElementsByTagName("citation");
//				 NodeList fileTitle = doc.getElementsByTagName("name");
//				 fileMap.put(curfile,fileTitle.item(0).getTextContent().replaceAll(",", ""));
				 for(int i=0;i<citations.getLength();i++) {
					 Element item = (Element) citations.item(i);
					 NodeList links = item.getElementsByTagName("AustLII");
					 NodeList class_type = item.getElementsByTagName("class");
					 if(links.getLength() != 0) {
//						 fileTitle = doc.getElementsByTagName("tocase");
//						 System.out.println(class_type.item(0).getTextContent() + " " + links.item(0).getTextContent());
						 String filename = getFileName(item.getElementsByTagName("AustLII").item(0).getTextContent());
						 if(filename != null) {
							 if(hash.get(curfile) == null)
								 hash.put(curfile, new HashSet<Citation>());
							 hash.get(curfile).add(new Citation(class_type.item(0).getTextContent(), filename));
//							 fileMap.put(filename,fileTitle.item(i).getTextContent().replaceAll(",", "")); 
//							 System.out.println(filename + " " + fileTitle.item(i).getTextContent().replaceAll(",", ""));
						 }
					 }
				 }
			}catch(Exception e) {
//				System.out.println("Encountered Exception in " + f.getName());
//				System.out.println(e.getMessage());
			}
		}
	}
	
	static HashMap<String,String> generateNodeFile(String dir, DocumentBuilderFactory dbFactory){
		File folder = new File(dir);
		File files[] = folder.listFiles();
		HashMap<String,String> hash = new HashMap<>();
        for(File f:files) {
        	try {
	        	String curfile = f.getName().split(".xml")[0];
				dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(f);
				NodeList fileNames = doc.getElementsByTagName("name");
			    hash.put(curfile, fileNames.item(0).getTextContent().replaceAll(",", ""));
        	}catch(Exception e) {
    			System.out.println(e.getMessage());
    		}
        }
        
		try {
			PrintWriter pw = new PrintWriter(new File("NodeList.csv"));
	        StringBuilder sb = new StringBuilder();
	        sb.append("Case Name,");
	        sb.append("Case Title,");
	        sb.append("\n");
	        for(String s:hash.keySet()) {
	        	sb.append(s+",");
	        	sb.append(hash.get(s)+"\n");
	        }
	        pw.write(sb.toString());
	        pw.close();
		}catch(Exception e) {
			
		}
		return hash;
	}
	
	static void writeToCSV(HashMap<String,HashSet<Citation>> hash, HashMap<String,String> fileMap) throws Exception {
		PrintWriter pw = new PrintWriter(new File("EdgeList-1.csv"));
        StringBuilder sb = new StringBuilder();
        sb.append("Source,");
        sb.append("Target,");
        sb.append("Citation Type,");
        sb.append("Source Title,");
        sb.append("Target Title");
        sb.append("\n");
        for(String s:hash.keySet()) {
			 for(Citation c: hash.get(s)) {
				 if(fileMap.get(c.file_name) != null) {
					 sb.append(s+",");
					 sb.append(c.file_name + ",");
					 sb.append(c.citation_type +",");
					 sb.append(fileMap.get(s) + ",");
					 sb.append(fileMap.get(c.file_name) + ",");
					 sb.append("\n");
				 }
			 }
		}
        pw.write(sb.toString());
        pw.close();
	}
}


class Citation{
	String citation_type;
	String file_name;
	
	public Citation(String c, String f) {
		citation_type = c;
		file_name = f;
	}
}
