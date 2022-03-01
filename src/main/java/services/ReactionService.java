package services;

import java.time.LocalDateTime;
import java.util.List;

import actions.views.EmployeeConverter;
import actions.views.EmployeeView;
import actions.views.ReactionConverter;
import actions.views.ReactionView;
import constants.JpaConst;
import models.Reaction;
import models.validators.ReactionValidator;

/**
 * リアクションテーブルの操作に関わる処理を行うクラス
 */
public class ReactionService extends ServiceBase {

    /**
     * 指定した従業員が作成したリアクションデータを、指定されたページ数の一覧画面に表示する分取得しReactionViewのリストで返却する
     * @param employee 従業員
     * @param page ページ数
     * @return 一覧画面に表示するデータのリスト
     */
    public List<ReactionView> getMinePerPage(EmployeeView employee, int page) {

        List<Reaction> reactions = em.createNamedQuery(JpaConst.Q_REP_GET_ALL_MINE, Reaction.class)
                .setParameter(JpaConst.JPQL_PARM_EMPLOYEE, EmployeeConverter.toModel(employee))
                .setFirstResult(JpaConst.ROW_PER_PAGE * (page - 1))
                .setMaxResults(JpaConst.ROW_PER_PAGE)
                .getResultList();
        return ReactionConverter.toViewList(reactions);
    }

    /**
     * 指定した従業員が作成したリアクションデータの件数を取得し、返却する
     * @param employee
     * @return リアクションデータの件数
     */
    public long countAllMine(EmployeeView employee) {

        long count = (long) em.createNamedQuery(JpaConst.Q_REP_COUNT_ALL_MINE, Long.class)
                .setParameter(JpaConst.JPQL_PARM_EMPLOYEE, EmployeeConverter.toModel(employee))
                .getSingleResult();

        return count;
    }

    /**
     * 指定されたページ数の一覧画面に表示するリアクションデータを取得し、ReactionViewのリストで返却する
     * @param page ページ数
     * @return 一覧画面に表示するデータのリスト
     */
    public List<ReactionView> getAllPerPage(int page) {

        List<Reaction> reactions = em.createNamedQuery(JpaConst.Q_REP_GET_ALL, Reaction.class)
                .setFirstResult(JpaConst.ROW_PER_PAGE * (page - 1))
                .setMaxResults(JpaConst.ROW_PER_PAGE)
                .getResultList();
        return ReactionConverter.toViewList(reactions);
    }

    /**
     * リアクションテーブルのデータの件数を取得し、返却する
     * @return データの件数
     */
    public long countAll() {
        long reactions_count = (long) em.createNamedQuery(JpaConst.Q_REA_COUNT, Long.class)
                .getSingleResult();
        return reactions_count;
    }

    /**
     * idを条件に取得したデータをReactionViewのインスタンスで返却する
     * @param id
     * @return 取得データのインスタンス
     */
    public ReactionView findOne(int id) {
        return ReactionConverter.toView(findOneInternal(id));
    }

    /**
     * 画面から入力されたリアクションの登録内容を元にデータを1件作成し、日報テーブルに登録する
     * @param rv リアクションの登録内容
     * @return バリデーションで発生したエラーのリスト
     */
    public List<String> create(ReactionView rv) {
        List<String> errors = ReactionValidator.validate(rv);
        if (errors.size() == 0) {
            LocalDateTime ldt = LocalDateTime.now();
            rv.setCreatedAt(ldt);
            rv.setUpdatedAt(ldt);
            createInternal(rv);
        }

        //バリデーションで発生したエラーを返却（エラーがなければ0件の空リスト）
        return errors;
    }

    /**
     * 画面から入力されたリアクションの登録内容を元に、リアクションデータを更新する
     * @param rv リアクションの更新内容
     * @return バリデーションで発生したエラーのリスト
     */
    public List<String> update(ReactionView rv) {

        //バリデーションを行う
        List<String> errors = ReactionValidator.validate(rv);

        if (errors.size() == 0) {

            //更新日時を現在時刻に設定
            LocalDateTime ldt = LocalDateTime.now();
            rv.setUpdatedAt(ldt);

            updateInternal(rv);
        }

        //バリデーションで発生したエラーを返却（エラーがなければ0件の空リスト）
        return errors;
    }

    /**
     * idを条件にデータを1件取得する
     * @param id
     * @return 取得データのインスタンス
     */
    private Reaction findOneInternal(int id) {
        return em.find(Reaction.class, id);
    }

    /**
     * リアクションデータを1件登録する
     * @param rv リアクションデータ
     */
    private void createInternal(ReactionView rv) {

        em.getTransaction().begin();
        em.persist(ReactionConverter.toModel(rv));
        em.getTransaction().commit();

    }

    /**
     * リアクションデータを更新する
     * @param rv リアクションデータ
     */
    private void updateInternal(ReactionView rv) {

        em.getTransaction().begin();
        Reaction r = findOneInternal(rv.getId());
        ReactionConverter.copyViewToModel(r, rv);
        em.getTransaction().commit();

    }

}