package board.board.entity;

import java.time.LocalDateTime;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
 
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

//@Entity
//@Table(name="t_jpa_board")
//@NoArgsConstructor
//@Data

@Entity
@Table(name="t_jpa_board")
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@Data
@ToString
public class BoardEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int boardIdx;
	
	@Column(nullable=false)
	private String title;
	
	@Column(nullable=false)
	private String contents;
	
	@Column(nullable=false)
	private int hitCnt = 0;
	
	@Column(nullable=false)
	private String creatorId;
	
	@Column(nullable=false)
	private String createdDatetime;
	
	private String updaterId;
	
	private String updatedDatetime;
	
//	@OneToMany(fetch= FetchType.EAGER, cascade=CascadeType.ALL)
//	@JoinColumn(name="boardIdx")
//	private Collection<BoardFileEntity> fileList;
	private String fileList;
	
}
