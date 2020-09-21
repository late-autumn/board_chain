-- creates table
function constructor()
  --게시판 테이블

  db.exec([[create table if not exists t_jpa_board(
        boardIdx INTEGER PRIMARY KEY AUTOINCREMENT,
        title text,
        contents text,
        hitCnt INTEGER,
        creatorId text,
        created_datetime text
    )]])

--    updated_datetime text,
--    updater_id text

    --게시판 파일 테이블
    db.exec([[create table if not exists t_file(
          idx INTEGER PRIMARY KEY AUTOINCREMENT,
          board_idx INTEGER,
          original_file_name text,
          stored_file_path text,
          file_size INTEGER,
          creator_id text,
          created_datetime INTEGER,
          updator_id text,
          updated_datetime INTERGER,
          deleted_yn text
    )]])

    db.exec([[INSERT INTO t_jpa_board (boardIdx, title, contents, hitCnt, creatorId, created_datetime) VALUES('
    1', 'hi title', 'hello world', '1', 'admin', '20120201021')]])

    --트랜잭션 저장
--    db.exec([[create table if not exists boardTxHistory(
  --        boardidx INTEGER,
    --      txhash text
--    )]])

end


--게시글 생성
function BoardCreate(title, contents, creatorId)
--function BoardCreate(contents, creatorId, title)
  db.exec("INSERT INTO t_jpa_board (title, contents, hitCnt, creatorId, created_datetime) VALUES('"
  .. title .. "', '".. contents .. "', '0', '".. creatorId .. "', '" .. system.getTimestamp() .. "')")


  local last_insert = 0;
  local rs = db.query("select max(boardIdx) as boardIdx from t_jpa_board")
  while rs:next() do
    last_insert = rs:get()
  end

  return last_insert



  --  .. hit_cnt .. " " .. system.getTimestamp() .. "', '"


  --local last_insert = 0;
--  local rs = db.query("select max(board_idx) as board_idx from t_jpa_board")
--  while rs:next() do
--    last_insert = rs:get()
--  end
--  return last_insert
end


--게시글 상세보기
function selectBoardDetail(boardIdx)
  local rt = {}
  local rs = db.query("select boardIdx, title, contents, hitCnt, creatorId, created_datetime FROM t_jpa_board WHERE boardIdx = ?", boardIdx)
  while rs:next() do
    local col1, col2, col3, col4, col5, col6  = rs:get()
    local item = {
        boardIdx = col1,
        title = col2,
        contents = col3,
        hitCnt= col4,
        creatorId = col5,
        created_datetime = col6
    }
    table.insert(rt, item)
  end
  return rt
end


--게시글 리스트
function selectBoardList()
  local returnstr = ""
  local rt = {}
  local rs = db.query("SELECT boardIdx, title, contents, hitCnt, creatorId, created_datetime FROM t_jpa_board ORDER BY boardIdx DESC")

  while rs:next() do
    local col1, col2, col3, col4, col5, col6= rs:get()
    local item = {
        boardIdx = col1,
        title = col2,
        contents = col3,
        hitCnt = col4,
        creatorId = col5,
        created_datetime = col6
    }
    table.insert(rt, item)
  end

  if #rt == 0 then
    returnstr = "empty"
    return returnstr
  end
  return rt
end








abi.register(BoardCreate, selectBoardDetail, selectBoardList)
