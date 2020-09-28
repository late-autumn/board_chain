package board.board.controller;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hibernate.service.spi.InjectService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import board.board.entity.BoardEntity;
import board.board.entity.BoardFileEntity;
import board.board.service.BoardServiceImpl;
import board.board.service.UserService;
import board.common.FileUtils;
import hera.api.model.ContractResult;

@Controller
public class UserController {
 

	protected final Logger logger = getLogger(getClass());
	
 

	@Autowired
	private UserService userService;

	
	@RequestMapping(value = "/jpa/register", method = RequestMethod.POST)
	public String postRegister(HttpServletResponse response, HttpServletRequest request) {
		logger.info("post register");
		
	//	int result = userService.idChk(request);
		
		return "redirect:/";
	}
	
	@RequestMapping(value="/jpa/login",method= RequestMethod.POST)
	public String login(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		logger.info("post login");
		session.getAttribute("member");
		
		return "redirect:/";
	}
	
 
		 
}