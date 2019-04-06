package at.or.wolfram.mate.familytree.service.familytree;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import at.or.wolfram.mate.familytree.common.Mapper;
import at.or.wolfram.mate.familytree.common.Tools;
import at.or.wolfram.mate.familytree.model.Tree;
import at.or.wolfram.mate.familytree.service.location.LocationLookupService;

public class FamilyTreeService {
	
	private final Crawler crawler;
	
	private static final Logger logger = Logger.getLogger(FamilyTreeService.class);
	
	private final File cacheFile;
	private Tree tree;
	
	public FamilyTreeService(String baseUrl, String cacheFileName, LocationLookupService locationLookupService) {
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
//			this.tree = this.crawler.crawlAncestors("per00001.htm");
//			this.tree = this.crawler.crawlSelected("per00072.htm"); // ROSOS Rozina
			this.tree = this.crawler.crawlIndices();
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
