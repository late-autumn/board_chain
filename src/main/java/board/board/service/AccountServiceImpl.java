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
import board.board.entity.AccountEntity;
import board.board.repository.AccountRepository;
import board.board.smartcontract.SmartContract;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxReceipt;
import hera.client.AergoClient;
import hera.wallet.WalletApi;

@Service
public class AccountServiceImpl implements AccountService {

	@Value("${forms.sc.id}")
	private String SCID;

	@Value("${forms.from.encryptionkey}")
	private String From_encPrivateKey;

	@Value("${forms.from.password}")
	private String From_password;

	SmartContract sc = new SmartContract();
	protected final Logger logger = getLogger(getClass());

	@Autowired
	AccountRepository accountRepository;

//	  @Override 
//	  public AccountEntity loadUserByUsername(String email) throws Exception{ 
//		  Optional<AccountEntity> optional = accountRepository.findByEmail(email);
//		  if(optional.isPresent()) {
//			  AccountEntity account = optional.get();
//			  return account;
//		  }
//		  else {
//			  throw new NullPointerException();
//		  }
//	  }

//	@Override
//	public List<BoardEntity> selectBoardList() throws Exception {
//		return boardRepository.findAllByOrderByBoardIdxDesc();
//	}

	@Override
	public void saveAccount(AccountEntity account){		
 		accountRepository.save(account);
	}

//	@Override
//	public BoardEntity selectBoardDetail(int boardIdx) throws Exception {
//		Optional<BoardEntity> optional = boardRepository.findById(boardIdx);
//		if (optional.isPresent()) {
//			BoardEntity board = optional.get();
//			board.setHitCnt(board.getHitCnt() + 1);
//			boardRepository.save(board);
//
//			return board;
//		} else {
//			throw new NullPointerException();
//		}
//	}
//
//	@Override
//	public void deleteBoard(int boardIdx) {
//		boardRepository.deleteById(boardIdx);
//	}
//
//	@Override
//	public BoardFileEntity selectBoardFileInformation(int boardIdx, int idx) throws Exception {
//		BoardFileEntity boardFile = boardRepository.findBoardFile(boardIdx, idx);
//		return boardFile;
//	}

	////////////////////////////////////////////////////

	public int register(Map<?, ?> params) {
		
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
			contractExecute = sc.contractExecute(walletApi, SCID, "createAccount", strArray);

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		aergoClient.close();

		return Integer.parseInt(contractExecute.getRet().toString());

	}

	public ContractResult checkAccountEmail(String email) {
		// 스마트컨트랙트 연결.
		// make aergo client
		AergoClient aergoClient = new Common().aergoclient();
		WalletApi walletApi = new Common().aergoKeystore(From_encPrivateKey, From_password, aergoClient);

		String args[] = { email };
		ContractResult contractResult = null;
		try {
			// 조회
			contractResult = sc.contractQuery(walletApi, SCID, "checkAccountEmail", args);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// close the client
		aergoClient.close();

		return contractResult;
	}


	
	public ContractResult login(String email, String password) {
		// 스마트컨트랙트 연결.
		// make aergo client
		AergoClient aergoClient = new Common().aergoclient();
		WalletApi walletApi = new Common().aergoKeystore(From_encPrivateKey, From_password, aergoClient);

		String args[] = { email, password };
		ContractResult contractResult = null;
		try {
			// 조회
			contractResult = sc.contractQuery(walletApi, SCID, "selectAccountLogin", args);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// close the client
		aergoClient.close();

		return contractResult;
	}
   	  
	

}