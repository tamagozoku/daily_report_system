package actions.views;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * リアクション情報について画面の入力値・出力値を扱うViewモデル
 *
 */
@Getter //全てのクラスフィールドについてgetterを自動生成する(Lombok)
@Setter //全てのクラスフィールドについてsetterを自動生成する(Lombok)
@NoArgsConstructor //引数なしコンストラクタを自動生成する(Lombok)
@AllArgsConstructor //全てのクラスフィールドを引数にもつ引数ありコンストラクタを自動生成する(Lombok)
public class ReactionView {

    /**
     * id
     */
    private Integer id;

    /**
     * リアクションを登録した従業員
     */
    private EmployeeView employee;

    /**
    * リアクションスタンプの種類
    */
   private String stamp;


    /**
     * いつのリアクションかを示す日付
     */
    private LocalDate reactionDate;

    /**
     * リアクションのタイトル
     */
    private String title;

    /**
     * リアクションの内容
     */
    private String content;

    /**
     * 登録日時
     */
    private LocalDateTime createdAt;

    /**
     * 更新日時
     */
    private LocalDateTime updatedAt;
}