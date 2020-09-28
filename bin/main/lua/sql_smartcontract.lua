-- creates table
function constructor()
  --게시판 테이블

  db.exec([[create table if not exists Board(
        boardIdx INTEGER PRIMARY KEY AUTOINCREMENT,
        title text,
        contents text,
        hitCnt INTEGER,
        creatorId text,
        createdDatetime datetime
    )]])

--    updated_datetime text,
--    updater_id text

    --게시판 파일 테이블
    db.exec([[create table if not exists BoardFile(
          idx INTEGER PRIMARY KEY AUTOINCREMENT,
          boardIdx INTEGER,
          originalFileName text,
          storedFilePath text,
          fileSize INTEGER,
          FOREIGN KEY(boardIdx) REFERENCES Board(boardIdx)
    )]])

    db.exec([[INSERT INTO Board (boardIdx, title, contents, hitCnt, creatorId, createdDatetime) VALUES('
    1', 'hi title', 'hello world', '1', 'admin', '1600750615')]])

    --트랜잭션 저장
    db.exec([[create table if not exists boardTxHistory(
          boardIdx INTEGER,
          txhash text
    )]])

    --회원 테이블
    db.exec([[create table if not exists boardUser(
          idx INTEGER PRIMARY KEY AUTOINCREMENT,
          userType INTEGER,
          userId text,
          userPassword text
    )]])


end


--게시글 생성
--function createBoard(title, contents, creatorId, ...)
function createBoard(title, contents, creatorId, originalFileName, storedFilePath, fileSize)
  db.exec("INSERT INTO Board (title, contents, hitCnt, creatorId, createdDatetime) VALUES('"
  .. title .. "', '".. contents .. "', '0', '".. creatorId .. "', '" .. system.getTimestamp() .. "')")



  local last_insert = 0;
  local rs = db.query("select max(boardIdx) as boardIdx from Board")
  while rs:next() do
    last_insert = rs:get()
  end

--  for i = 1, select('#', ...) do
--    db.exec("INSERT INTO BoardFile (boardIdx, originalFileName, storedFilePath, fileSize) VALUES('"
--    .. last_insert .. "', '"
--    .. select(i, ...) .. "')")
--  end

  db.exec("INSERT INTO BoardFile (boardIdx, originalFileName, storedFilePath, fileSize) VALUES('"
  .. last_insert.."','".. originalFileName .. "','".. storedFilePath .. "','".. fileSize .."')")

  return last_insert

end


--게시글 생성_항목이미지 저장
function createBoardImages(boardIdx, ...)
  local rt = {}
  local rs = db.query("SELECT idx FROM BoardFile WHERE boardIdx = ? ORDER BY idx ASC", boardIdx)
  local asd = ""
  local itemIndex = 1

  while rs:next() do
    asd = rs:get()
    local col1= rs:get()
    db.exec("UPDATE BoardFile SET originalFileName = ?, storedFilePath = ?, fileSize = ?  WHERE boardIdx = ?",select((itemIndex*3)-2, ...),select((itemIndex*3)-1, ...),select((itemIndex*3), ...), boardIdx)
    itemIndex = itemIndex + 1
  end
  return asd
end



--게시글 생성_항목이미지 수정
function updateBoardImages(boardIdx, ...)
  local rt = {}
  local rs = db.query("SELECT idx FROM BoardFile WHERE boardIdx = ? ORDER BY idx ASC", boardIdx)
  local asd = ""
  local itemIndex = 1

  while rs:next() do
    asd = rs:get()
    local col1= rs:get()
    db.exec("UPDATE BoardFile SET originalFileName = ?, storedFilePath = ?, fileSize = ?  WHERE boardIdx = ?",select((itemIndex*4)-2, ...),select((itemIndex*4)-1, ...),select((itemIndex*4), ...), col1)
    itemIndex = itemIndex + 1
  end
  return asd
end










--게시글 리스트
function selectBoardList()
  local returnstr = ""
  local rt = {}
  local rs = db.query("SELECT boardIdx, title, contents, hitCnt, creatorId, createdDatetime FROM Board ORDER BY boardIdx DESC")

  while rs:next() do
    local col1, col2, col3, col4, col5, col6= rs:get()
    local item = {
        boardIdx = col1,
        title = col2,
        contents = col3,
        hitCnt = col4,
        creatorId = col5,
        createdDatetime = col6
    }
    table.insert(rt, item)
  end

  if #rt == 0 then
    returnstr = "empty"
    return returnstr
  end
  return rt
end


--게시글 조회수 증가
function increaseHitCnt(boardIdx)
  db.exec("UPDATE Board SET hitCnt=hitCnt+1 WHERE boardIdx=?", boardIdx)
end

--게시글 상세보기
function selectBoardDetail(boardIdx)
  local rt = {}
  local rs = db.query("SELECT boardIdx, title, contents, hitCnt, creatorId, createdDatetime FROM Board WHERE boardIdx = ?", boardIdx)

  while rs:next() do
    local col1, col2, col3, col4, col5, col6 = rs:get()
    local item = {
        boardIdx = col1,
        title = col2,
        contents = col3,
        hitCnt = col4,
        creatorId = col5,
        createdDatetime = col6
    }
    table.insert(rt, item)

  end

  return rt
end


--게시글 사진 상세보기
function selectBoardFileDetail(boardIdx)
  local rt = {}
  local rs = db.query("SELECT idx, originalFileName, storedFilePath, fileSize FROM BoardFile WHERE boardIdx = ? ORDER BY idx ASC", boardIdx)
  while rs:next() do
    local col1, col2, col3, col4 = rs:get()
    local item = {
        idx = col1,
        originalFileName = col2,
        storedFilePath = col3,
        fileSize = col4
    }
    table.insert(rt, item)
  end
  return rt
end





--게시글 수정
--function editBoard(boardIdx, title, contents, created_datetime, originalFileName, storedFilePath, fileSize)
--  db.exec("UPDATE Board SET title=?, contents=?, createdDatetime=? WHERE boardIdx=?",
--   title, contents,  system.getTimestamp(), boardIdx)

--   local idx = 0;
--   local itemIndex = 1;
--   local rs = db.query("SELECT idx FROM BoardFile WHERE boardIdx = ? ORDER BY idx ASC", boardIdx)

--   while rs:next() do
--     idx = rs:get()

--     db.exec("UPDATE BoardFile SET originalFileName = ?, storedFilePath = ?, fileSize = ?  WHERE idx=?",
--             originalFileName, storedFilePath, fileSize, idx)
--     itemIndex = itemIndex + 1
--   end
--   return itemIndex
-- end

--게시글 수정
function editBoard(boardIdx, title, contents, originalFileName, storedFilePath, fileSize)
  db.exec("UPDATE Board SET title=?, contents=?, createdDatetime=? WHERE boardIdx=?",
   title, contents,  system.getTimestamp(), boardIdx)

     db.exec("UPDATE BoardFile SET originalFileName = ?, storedFilePath = ?, fileSize = ?  WHERE boardIdx=?",
             originalFileName, storedFilePath, fileSize, boardIdx)
 end



--게시글 삭제
function deleteBoard(boardIdx)
  db.exec("DELETE FROM BoardFile WHERE boardIdx=?",boardIdx)
  db.exec("DELETE FROM Board WHERE boardIdx=?",boardIdx)
end






abi.register(createBoard, selectBoardDetail, selectBoardList, editBoard, increaseHitCnt, deleteBoard, createBoardImages, selectBoardFileDetail)
