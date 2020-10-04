package board.board.controller;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.net.HttpHeaders;


@RestController
//@RequestMapping("/file")
@RequestMapping(value = {"/file", "/images"})
public class FileController {
	
	
	private static final String filePath = "/Users/owen/toy/board_chain/images/";
	
	
	@GetMapping("/{path}/{name}") 
	ResponseEntity<Resource> download(@PathVariable String path, @PathVariable String name, HttpServletRequest req) {
		
		Path _path = Paths.get(filePath + "/" + path + "/" + name).normalize();
		try {
			Resource resource = new UrlResource(_path.toUri());
			
			String contentType = req.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
			
			return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename\""+resource.getFilename()+"\"")
					.body(resource);
					
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return null;
		
	}
	
	

}
