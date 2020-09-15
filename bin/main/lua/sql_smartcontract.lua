-- creates table
function constructor()
  --투표 테이블
  db.exec([[create table if not exists Vote(
        voteidx INTEGER PRIMARY KEY AUTOINCREMENT,
        votename text,
        votedescription text,
        regdate text,
        startdate text,
        enddate text,
        status text,
        openoffstatus text,
        monitorstatus text,
        namestatus text,
        sexstatus text,
        agestatus text,
        jobstatus text
    )]])

    --투표 히스토리 테이블
    db.exec([[create table if not exists VoteHistory(
          votehistory text
    )]])

    --투표 아이템 테이블
    db.exec([[create table if not exists VoteItem(
          voteitemidx INTEGER PRIMARY KEY AUTOINCREMENT,
          voteidx INTEGER,
          item text,
          count INTEGER DEFAULT 0,
          imgurl text,
          imgname text,
          imgoriginalname text,
          imgsize INTEGER,
          FOREIGN KEY(voteidx) REFERENCES Vote(voteidx)
    )]])

    --사용자 데이터 테이블
    db.exec([[create table if not exists UserData(
          userdataidx INTEGER PRIMARY KEY AUTOINCREMENT,
          voteidx INTEGER,
          name text,
          sex text,
          age INTEGER,
          job test
    )]])

    --트랜잭션 저장
    db.exec([[create table if not exists voteTxHistory(
          voteidx INTEGER,
          txhash text
    )]])

end


--관리자 투표생성
function AdminVoteCreate(votename, startdate, starttime, enddate, endtime, status, openoffstatus, monitorstatus, votedescription, namestatus, sexstatus, agestatus, jobstatus, ...)
  db.exec("INSERT INTO Vote (votename, startdate, enddate, votedescription, regdate, status, openoffstatus, monitorstatus, namestatus, sexstatus, agestatus, jobstatus) VALUES('"
  .. votename .. "', '"
  .. startdate .. " " .. starttime .. "', '"
  .. enddate .. " " .. endtime .. "', '"
  .. votedescription .. "', '"
  .. system.getTimestamp() .. "', '"
  .. status .. "', '"
  .. openoffstatus .. "', '"
  .. monitorstatus .. "', '"
  .. namestatus .. "', '"
  .. sexstatus .. "', '"
  .. agestatus .. "', '"
  .. jobstatus .. "')")

  local last_insert = 0;
  local rs = db.query("select max(voteidx) as voteidx from Vote")
  while rs:next() do
    last_insert = rs:get()
  end

  for i = 1, select('#', ...) do
    db.exec("INSERT INTO VoteItem (voteidx, item) VALUES('"
    .. last_insert .. "', '"
    .. select(i, ...) .. "')")
  end

  return last_insert
end

--관리자 투표생성_항목이미지 저장
function AdminVoteCreate_Item_Images(voteidx, ...)
  local rt = {}
  local rs = db.query("SELECT voteitemidx FROM VoteItem WHERE voteidx = ? ORDER BY voteitemidx ASC", voteidx)
  local asd = ""
  local itemIndex = 1

  while rs:next() do
    asd = rs:get()
    local col1= rs:get()
    db.exec("UPDATE VoteItem SET imgurl = ?, imgname = ?, imgoriginalname = ?, imgsize = ?  WHERE voteitemidx = ?",select((itemIndex*4)-3, ...),select((itemIndex*4)-2, ...),select((itemIndex*4)-1, ...),select((itemIndex*4), ...), col1)
    itemIndex = itemIndex + 1
  end
  return asd
end


--관리자 투표 상세보기_투표
function AdminVoteDetail(voteidx)
  local rt = {}
  local rs = db.query("select voteidx, votename, votedescription, regdate, status, startdate, enddate, openoffstatus, monitorstatus, namestatus, sexstatus, agestatus, jobstatus FROM Vote WHERE voteidx = ?", voteidx)
  while rs:next() do
    local col1, col2, col3, col4, col5, col6, col7, col8, col9, col10, col11, col12, col13= rs:get()
    local item = {
        voteidx = col1,
        votename = col2,
        votedescription = col3,
        regdate = col4,
        status = col5,
        startdate = col6,
        enddate = col7,
        openoffstatus = col8,
        monitorstatus = col9,
        namestatus = col10,
        sexstatus = col11,
        agestatus = col12,
        jobstatus = col13
    }
    table.insert(rt, item)
  end
  return rt
end


--관리자 투표 상세보기_아이템
function AdminVoteItemDetail(voteidx)
  local rt = {}
  local rs = db.query("SELECT voteitemidx, item, count, imgurl, imgname, imgoriginalname, imgsize FROM VoteItem WHERE voteidx = ? ORDER BY voteitemidx ASC", voteidx)
  while rs:next() do
    local col1, col2, col3, col4, col5, col6, col7= rs:get()
    local item = {
        voteitemidx = col1,
        item = col2,
        count = col3,
        imgurl = col4,
        imgname = col5,
        imgoriginalname = col6,
        imgsize = col7
    }
    table.insert(rt, item)
  end
  return rt
end


--사용자 투표 상세보기
function UserVoteDetail(voteidx)
  local rt = {}
  local rs = db.query("select voteidx, votename, votedescription, regdate, status, startdate, enddate, openoffstatus, monitorstatus, namestatus, sexstatus, agestatus, jobstatus FROM Vote WHERE voteidx = ? AND openoffstatus='Y'", voteidx)
  while rs:next() do
    local col1, col2, col3, col4, col5, col6, col7, col8, col9, col10, col11, col12, col13= rs:get()
    local item = {
        voteidx = col1,
        votename = col2,
        votedescription = col3,
        regdate = col4,
        status = col5,
        startdate = col6,
        enddate = col7,
        openoffstatus = col8,
        monitorstatus = col9,
        namestatus = col10,
        sexstatus = col11,
        agestatus = col12,
        jobstatus = col13
    }
    table.insert(rt, item)
  end
  return rt
end

--사용자 투표 상세보기_아이템
function UserVoteItemDetail(voteidx)
  local rt = {}
  local rs = db.query("SELECT voteitemidx, item, count, imgurl, imgname, imgoriginalname, imgsize FROM VoteItem WHERE voteidx = ? ORDER BY voteitemidx ASC", voteidx)
  while rs:next() do
    local col1, col2, col3, col4, col5, col6, col7= rs:get()
    local item = {
        voteitemidx = col1,
        item = col2,
        count = col3,
        imgurl = col4,
        imgname = col5,
        imgoriginalname = col6,
        imgsize = col7
    }
    table.insert(rt, item)
  end
  return rt
end


--사용자 투표 중복체크
function UserVoteCheck(pubkey, voteidx)
  local returnStatus = "";
  local pubkey_voteidx = pubkey.."_"..voteidx
  local rt = {}
  local rs = db.query("SELECT COUNT(votehistory) as count FROM Votehistory WHERE votehistory = ?" , pubkey_voteidx)
  while rs:next() do
    local count = rs:get()
    if rs:get() > 0 then
      return true
    else
      return false
    end
  end
end


--사용자 투표참여
function UserVoteGo(voteitemidx, pubkey, voteidx, name, sex, age, job, voteitemname)
  local pubkey_voteidx = pubkey.."_"..voteidx
  local status  = ""; --Y : 투표중, N : 투표종료 , S : 투표중지
  local returnStatus = ""; --Y : 투표성공, N : 투표종료 , S : 투표중지, D: 중복투표
  --투표 참여 전 투표가 진행 중인지 확인. ( 상태값 확인 )
  local rt = {}
  local rs = db.query("SELECT status FROM Vote WHERE voteidx = ?" , voteidx)
  while rs:next() do
    status = rs:get()
  end

  if status ~= 'Y'
  then
    returnStatus = status
    return returnStatus
  end

  --중복 투표 확인
  local rt_1 = {}
  local rs_1 = db.query("SELECT COUNT(votehistory) as count FROM Votehistory WHERE votehistory = ?" , pubkey_voteidx)
  while rs_1:next() do
    local count = rs_1:get()
    if rs_1:get() > 0 then
      returnStatus = "D"
      return returnStatus
    end
  end


  db.exec("UPDATE VoteItem SET count=count+1 WHERE voteitemidx = ?", voteitemidx)
  db.exec("INSERT INTO VOTEHISTORY VALUES (?)",pubkey_voteidx)
  db.exec("INSERT INTO USERDATA (voteidx, name, sex, age, job) VALUES('"
  .. voteidx .. "', '"
  .. name .. "', '"
  .. sex .. "', '"
  .. age .. "', '"
  .. job .. "')")

  returnStatus = "Y"
  return returnStatus;
end


--관리자 투표 리스트
function AdminVoteList()
  local returnstr = ""
  local rt = {}
  local rs = db.query("SELECT voteidx, votename, votedescription, regdate, status, startdate, enddate, openoffstatus, monitorstatus FROM Vote ORDER BY regdate DESC")

  while rs:next() do
    local col1, col2, col3, col4, col5, col6, col7, col8, col9= rs:get()
    local item = {
        voteidx = col1,
        votename = col2,
        votedescription = col3,
        regdate = col4,
        status = col5,
        startdate = col6,
        enddate = col7,
        openoffstatus = col8,
        monitorstatus = col9
    }
    table.insert(rt, item)
  end

  if #rt == 0 then
    returnstr = "empty"
    return returnstr
  end
  return rt
end


--사용자 투표 리스트
function UserVoteList()
  local returnstr = ""
  local rt = {}
  local rs = db.query("SELECT voteidx, votename, votedescription, regdate, status, startdate, enddate, openoffstatus, monitorstatus FROM Vote WHERE openoffstatus = 'Y' ORDER BY regdate DESC")

  while rs:next() do
    local col1, col2, col3, col4, col5, col6, col7, col8, col9= rs:get()
    local item = {
        voteidx = col1,
        votename = col2,
        votedescription = col3,
        regdate = col4,
        status = col5,
        startdate = col6,
        enddate = col7,
        openoffstatus = col8,
        monitorstatus = col9
    }
    table.insert(rt, item)
  end

  if #rt == 0 then
    returnstr = "empty"
    return returnstr
  end
  return rt
end


function HistorySelect()
  local rt = {}
  local rs = db.query("SELECT votehistory FROM VoteHistory")
  while rs:next() do
    local col1= rs:get()
    local item = {
        votehistory = col1
    }
    table.insert(rt, item)
  end
  return rt
end


--관리자 투표 수정
function AdminVoteEdit(voteidx, votename, startdate, starttime, enddate, endtime, status, openoffstatus, monitorstatus, votedescription, namestatus, sexstatus, agestatus, jobstatus, ...)
  db.exec("UPDATE Vote SET votename=?, startdate=?, enddate=?, status=?, openoffstatus=?, monitorstatus=?, votedescription=?, namestatus=?, sexstatus=?, agestatus=?, jobstatus=?  WHERE voteidx=?", votename, startdate.." "..starttime, enddate.." "..endtime, status, openoffstatus, monitorstatus, votedescription, namestatus, sexstatus, agestatus, jobstatus, voteidx)

  local voteitemidx = 0;
  local itemIndex = 1;
  local rs = db.query("SELECT voteitemidx FROM voteItem WHERE voteidx = ? ORDER BY voteitemidx ASC", voteidx)
  while rs:next() do
    voteitemidx = rs:get()

    db.exec("UPDATE VoteItem SET item=? WHERE voteitemidx = ?", select(itemIndex, ...), voteitemidx)
    itemIndex = itemIndex + 1
  end
  return itemIndex
end


--투표상태 스케쥴러 업데이트
function AdminVoteStatusUpdate(date)
  db.exec("UPDATE Vote SET status='F' WHERE enddate=?", date)
end

function UserDataList(voteidx)
  local rt = {}
  local rs = db.query("SELECT voteidx, name, sex, age, job FROM Userdata WHERE voteidx = ?", voteidx)
  while rs:next() do
    local col1, col2, col3, col4, col5= rs:get()
    local item = {
        voteidx = col1,
        name = col2,
        sex = col3,
        age = col4,
        job = col5
    }
    table.insert(rt, item)
  end
  if #rt == 0 then
    returnstr = "empty"
    return returnstr
  end
  return rt
end

--사용자 투표시, 생성된 트랜잭션 저장
function InsertTxHistory(voteidx, txhash)
db.exec("INSERT INTO voteTxHistory (voteidx, txhash) VALUES('"
.. voteidx .. "', '"
.. txhash .. "')")
end

--관리자 투표 트랜잭션 조회
function AdminTxSelect(voteidx)
  local rt = {}
  local rs = db.query("SELECT txhash FROM voteTxHistory WHERE voteidx = ?", voteidx)
  while rs:next() do
    local col1= rs:get()
    local item = {
        txhash = col1
    }
    table.insert(rt, item)
  end
  if #rt == 0 then
    returnstr = "empty"
    return returnstr
  end
  return rt
end

--사용자 투표 트랜잭션 조회
function UserTxSelect(voteidx)
  local rt = {}
  local rs = db.query("SELECT txhash FROM voteTxHistory WHERE voteidx = ?", voteidx)
  while rs:next() do
    local col1= rs:get()
    local item = {
        txhash = col1
    }
    table.insert(rt, item)
  end
  if #rt == 0 then
    returnstr = "empty"
    return returnstr
  end
  return rt
end



abi.register(AdminVoteCreate, AdminVoteDetail, AdminVoteItemDetail, UserVoteDetail, UserVoteItemDetail, UserVoteCheck, UserVoteGo, AdminVoteList, UserVoteList, AdminVoteEdit, AdminVoteStatusUpdate, HistorySelect, UserDataList, InsertTxHistory, AdminTxSelect, AdminVoteCreate_Item_Images, UserTxSelect)
