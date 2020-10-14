package board.board.service;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import board.board.common.Common;
import board.board.smartcontract.SmartContract;
import board.common.FileUtils;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxReceipt;
import hera.client.AergoClient;
import hera.wallet.WalletApi;

@Service
public class BoardServiceImpl implements BoardService {

	@Value("${forms.sc.id}")
	private String SCID;

	@Value("${forms.from.encryptionkey}")
	private String From_encPrivateKey;

	@Value("${forms.from.password}")
	private String From_password;

	SmartContract sc = new SmartContract();
	protected final Logger logger = getLogger(getClass());
 
	@Autowired
	FileUtils fileUtils;
 
	public int write(Map<?, ?> params) {
		
		// 스마트컨트랙트 연결.
		// make aergo client
		AergoClient aergoClient = new Common().aergoclient();
		WalletApi walletApi = new Common().aergoKeystore(From_encPrivateKey, From_password, aergoClient);

		// params map 데이터 가공.
		// map -> list -> 배열
		List<Object> list = new ArrayList();

		Set paramKeySet = params.keySet();
		Iterator keyIterator = paramKeySet.iterator();

		while (keyIterator.hasNext()) {
			String name = (String) keyIterator.next();
			String values[] = (String[]) params.get(name);
			for (int i = 0; i < values.length; i++) {
				list.add(values[i]);
			}
		}

		String[] strArray = list.toArray(new String[list.size()]);

		ContractTxReceipt contractExecute = null;
		try {
			contractExecute = sc.contractExecute(walletApi, SCID, "createBoard", strArray);

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		aergoClient.close();

		return Integer.parseInt(contractExecute.getRet().toString());

	}

	public ContractResult list() {
		// 스마트컨트랙트 연결.
		// make aergo client
		AergoClient aergoClient = new Common().aergoclient();
		WalletApi walletApi = new Common().aergoKeystore(From_encPrivateKey, From_password, aergoClient);

		String args[] = {};
		ContractResult contractResult = null;
		try {
			// 조회
			contractResult = sc.contractQuery(walletApi, SCID, "selectBoardList", args);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// close the client
		aergoClient.close();

		return contractResult;

	}

	
	public ContractResult view(int boardIdx) {
		// 스마트컨트랙트 연결.
		// make aergo client
		AergoClient aergoClient = new Common().aergoclient();
		WalletApi walletApi = new Common().aergoKeystore(From_encPrivateKey, From_password, aergoClient);

		String args[] = { String.valueOf(boardIdx) };
		ContractResult contractResult = null;
		try {
			// 조회
			contractResult = sc.contractQuery(walletApi, SCID, "selectBoardDetail", args);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// close the client
		aergoClient.close();

		return contractResult;
	}

	public void edit(Map<?, ?> params) {
		// 스마트컨트랙트 연결.
		// make aergo client
		AergoClient aergoClient = new Common().aergoclient();
		WalletApi walletApi = new Common().aergoKeystore(From_encPrivateKey, From_password, aergoClient);

		// params map 데이터 가공.
		// map -> list -> 배열
		List<Object> list = new ArrayList();
		Set paramKeySet = params.keySet();
		Iterator keyIterator = paramKeySet.iterator();
		while (keyIterator.hasNext()) {
			String name = (String) keyIterator.next();
			String values[] = (String[]) params.get(name);
			for (int i = 0; i < values.length; i++) {
				list.add(values[i]);
			}
		}
		String[] strArray = list.toArray(new String[list.size()]);

		System.out.println("strArray : " + strArray);

		ContractTxReceipt contractExecute = null;

		try {
			contractExecute = sc.contractExecute(walletApi, SCID, "editBoard", strArray);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		aergoClient.close();

	}

	
	  
	public void increaseHitCnt(int boardIdx) {
		AergoClient aergoClient = new Common().aergoclient();
		WalletApi walletApi = new Common().aergoKeystore(From_encPrivateKey, From_password, aergoClient);

		String args[] = { String.valueOf(boardIdx) };
		ContractTxReceipt contractExecute = null;
		try {
			// 조회
			contractExecute = sc.contractExecute(walletApi, SCID, "increaseHitCnt", args);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// close the client
		aergoClient.close();

	}

	public void delete(int boardIdx) {
		AergoClient aergoClient = new Common().aergoclient();
		WalletApi walletApi = new Common().aergoKeystore(From_encPrivateKey, From_password, aergoClient);

		String args[] = { String.valueOf(boardIdx) };
		ContractTxReceipt contractExecute = null;
		try {
			// 조회
			contractExecute = sc.contractExecute(walletApi, SCID, "deleteBoard", args);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// close the client
		aergoClient.close();

	}
 
//	  public void write_item_images(int boardIdx, MultipartHttpServletRequest mtfRequest, String filepath, HttpServletRequest request) {
//	    //String[] fileinfo = Common.fileupload(boardIdx, mtfRequest, filepath);
//	    String[] fileinfo = null;
//		try {
//			fileinfo = FileUtils.parseFileInfo(mtfRequest);
//		} catch (Exception e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//	    //스마트컨트랙트 연결.
//	    // make aergo client
//	    AergoClient aergoClient = new Common().aergoclient();
//	    WalletApi walletApi = new Common().aergoKeystore(From_encPrivateKey, From_password, aergoClient);
//
//	    ContractTxReceipt contractExecute = null;
//
//	    try {
//	      contractExecute = sc.contractExecute(walletApi, SCID, "createBoardImages", fileinfo);
//	    } catch (InterruptedException e) {
//	      // TODO Auto-generated catch block
//	      e.printStackTrace();
//	    }
//
//	    aergoClient.close();
//	  }
	  
	  public ContractResult view_Image(int boardIdx) {
		    //스마트컨트랙트 연결.
		    // make aergo client
		    AergoClient aergoClient = new Common().aergoclient();
		    WalletApi walletApi = new Common()
		        .aergoKeystore(From_encPrivateKey, From_password, aergoClient);

		    String args[] = {String.valueOf(boardIdx)};
		    ContractResult contractResult = null;
		    try {
		      //조회
		      contractResult = sc.contractQuery(walletApi, SCID, "selectBoardFileDetail", args);
		    } catch (InterruptedException e) {
		      // TODO Auto-generated catch block
		      e.printStackTrace();
		    }

		    // close the client
		    aergoClient.close();

		    return contractResult;
		  }

}