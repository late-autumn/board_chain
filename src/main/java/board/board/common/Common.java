package board.board.common;
  
import java.io.File;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import hera.api.model.Authentication;
import hera.client.AergoClient;
import hera.client.AergoClientBuilder;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import hera.keystore.InMemoryKeyStore;
import hera.keystore.KeyStore;
import hera.wallet.WalletApi;
import hera.wallet.WalletApiFactory;

public class Common {
  
  //테스트넷 접속 주소
  protected static String endpoint = "sql3.aergo.io:7845";

  // make aergo client
  public AergoClient aergoclient() {
    AergoClient aergoClient = new AergoClientBuilder()
        .withEndpoint(endpoint)
        .withNonBlockingConnect()
        .build();
    return aergoClient;
  }

  // 키 생성 함수
  public static AergoKey createAergoKey() {
    //키생성
    AergoKey key = new AergoKeyGenerator().create();

    //생성된 키와 비빌번호를 토대로 privateKey추출, password 부분은 필요한 값으로 수정하여 사용
    String encPrivatKey = key.export("password").toString();

    System.out.println(">>>>>>>> private encKey :: " + encPrivatKey);
    System.out.println(">>>>>>>> Address :: " + key.getAddress());

    return key;
  }

  // 키 임포트 함수
  public static AergoKey ImportAergoKey(final String encPrivateKey, final String password) {
    //키생성
    AergoKey key = AergoKey.of(encPrivateKey, password);

    //생성된 키와 비빌번호를 토대로 privateKey추출, password 부분은 필요한 값으로 수정하여 사용
    String encPrivatKey = key.export("password").toString();

    System.out.println(">>>>>>>> private encKey :: " + encPrivatKey);
    System.out.println(">>>>>>>> Address :: " + key.getAddress());

    return key;
  }

  public static WalletApi aergoKeystore(String encPrivateKey, String password,
      AergoClient aergoClient) {
    KeyStore keyStore = new InMemoryKeyStore();
    AergoKey key = AergoKey.of(encPrivateKey, password);
    Authentication authentication = Authentication.of(key.getAddress(), password);
    keyStore.save(authentication, key);

    // make wallet api
    WalletApi walletApi = new WalletApiFactory().create(keyStore);

    // bind aergo client
    walletApi.bind(aergoClient);

    // unlock account
    walletApi.unlock(authentication);

    return walletApi;
  }
  
  public static String[] fileupload(int voteidx, MultipartHttpServletRequest mtfRequest, String filepath) {

    List<MultipartFile> fileList = mtfRequest.getFiles("file");
    List<Object> list = new ArrayList();
    DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMdd"); 
	ZonedDateTime current = ZonedDateTime.now();
    //String path = filepath;
    String originalFileName = "";
    String storedFilePath = "";
    String fileSize = "";
    String path = "images/"+current.format(format);
    
    //운영

    //System.out.println("domain : " + domain);
    
    //개발
    //String tomcatpath = session.getServletContext().getRealPath("/");
    
    //System.out.println("tomcatpath : " + tomcatpath);

    list.add(Integer.toString(voteidx));
    for (MultipartFile mf : fileList) {
    	originalFileName = mf.getOriginalFilename(); // 원본 파일 명
    	fileSize =  Integer.toString((int)mf.getSize()); // 파일 사이즈
        
 
        try {
            mf.transferTo(new File(originalFileName));
            list.add(storedFilePath);
            list.add(originalFileName);
            list.add(fileSize);
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    String[] fileinfo = list.toArray(new String[list.size()]);
    System.out.println("fileinfo : " + Arrays.toString(fileinfo));
    
    return fileinfo;
  }

}
