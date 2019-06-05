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
			logger.warn("Cache file does not exist. This is normal on first run");
		}
	}
	
	public Tree generateAncestorTree(String rootPersonLink) {
		this.tree = this.crawler.crawlAncestors(rootPersonLink);
		try {
			writeToCacheFile();
		} catch (IOException e) {
			logger.error("Could not write cache file", e);
		}
		return this.tree;
	}
	
	public Tree generateTreeByIndex(String... whitelist) {
		this.tree = this.crawler.crawlIndices(whitelist);
		try {
			writeToCacheFile();
		} catch (IOException e) {
			logger.error("Could not write cache file", e);
		}
		return this.tree;
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
