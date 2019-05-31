package at.or.wolfram.mate.familytree.service.familytree;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import at.or.wolfram.mate.familytree.common.Mapper;
import at.or.wolfram.mate.familytree.common.Tools;
import at.or.wolfram.mate.familytree.model.Tree;
import at.or.wolfram.mate.familytree.service.location.LocationLookupService;

@Component
public class FamilyTreeService {
	
	private final Crawler crawler;
	
	private static final Logger logger = Logger.getLogger(FamilyTreeService.class);
	
	private final File cacheFile;
	private Tree tree;
	
	@Autowired
	public FamilyTreeService(
			@Value("${service.familytree.baseUrl}") String baseUrl,
			@Value("${service.familytree.cacheFileName}") String cacheFileName,
			LocationLookupService locationLookupService) {
		
		this.crawler = new Crawler(baseUrl, locationLookupService);
		
		this.cacheFile = new File(cacheFileName);
		if (this.cacheFile.exists()) {
			try {
				readFromCacheFile();
			} catch (IOException e) {
				logger.error("Could not read cache file", e);
			}
		}
		else {
			logger.info("Cache file does not exist. Starting crawler now.");
//			this.tree = this.crawler.crawlAncestors("per00001.htm"); // Mate
//			this.tree = this.crawler.crawlAncestors("per00049.htm"); // David
//			this.tree = this.crawler.crawlAncestors("per00034.htm"); // Csabi
//			this.tree = this.crawler.crawlAncestors("per00096.htm"); // Krisi
//			this.tree = this.crawler.crawlAncestors("per00039.htm"); // Toto
//			this.tree = this.crawler.crawlAncestors("per00036.htm"); // Viki
//			this.tree = this.crawler.crawlIndices("Wolfram", "Vágvölgyi");
//			this.tree = this.crawler.crawlIndices("Szajkó");
//			this.tree = this.crawler.crawlIndices("Körmendi", "Körmendy", "Körmöndi");
//			this.tree = this.crawler.crawlIndices("Niedermayer");
//			this.tree = this.crawler.crawlIndices("Ipsics", "Ipsits", "Illésfalvi", "Illésfalvy", "Iglódi");
//			this.tree = this.crawler.crawlIndices("Hannig");
			this.tree = this.crawler.crawlIndices("Timaffy");
//			this.tree = this.crawler.crawlIndices(); // mindenki
//			this.tree = this.crawler.crawlSelected("per00072.htm"); // ROSOS Rozina
			try {
				writeToCacheFile();
			} catch (IOException e) {
				logger.error("Could not write cache file", e);
			}
		}
	}
	
	public Tree getTree() {
		return this.tree;
	}
	
	private void readFromCacheFile() throws IOException {
		String jsonData = FileUtils.readFileToString(this.cacheFile, Tools.ENCODING);
		this.tree = Mapper.parseJson(jsonData, Tree.class);
	}
	
	private void writeToCacheFile() throws IOException {
		FileUtils.write(this.cacheFile, Mapper.writeJson(this.tree), Tools.ENCODING);
	}
	
}
