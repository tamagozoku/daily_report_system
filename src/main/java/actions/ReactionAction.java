package actions;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.ServletException;

import actions.views.EmployeeView;
import actions.views.ReactionView;
import constants.AttributeConst;
import constants.ForwardConst;
import constants.JpaConst;
import constants.MessageConst;
import services.ReactionService;

/**
 * リアクションに関する処理を行うActionクラス
 *
 */
public class ReactionAction extends ActionBase {

    private ReactionService service;

    /**
     * メソッドを実行する
     */
    @Override
    public void process() throws ServletException, IOException {

        service = new ReactionService();

        //メソッドを実行
        invoke();
        service.close();
    }

    /**
     * 一覧画面を表示する
     * @throws ServletException
     * @throws IOException
     */
    public void index() throws ServletException, IOException {

        //指定されたページ数の一覧画面に表示する日報データを取得
        int page = getPage();
        List<ReactionView> reactions = service.getAllPerPage(page);

        //全日報データの件数を取得
        long reactionsCount = service.countAll();

        putRequestScope(AttributeConst.REACTIONS, reactions); //取得したリアクションデータ
        putRequestScope(AttributeConst.REA_COUNT, reactionsCount); //全てのリアクションデータの件数
        putRequestScope(AttributeConst.PAGE, page); //ページ数
        putRequestScope(AttributeConst.MAX_ROW, JpaConst.ROW_PER_PAGE); //1ページに表示するレコードの数

        //セッションにフラッシュメッセージが設定されている場合はリクエストスコープに移し替え、セッションからは削除する
        String flush = getSessionScope(AttributeConst.FLUSH);
        if (flush != null) {
            putRequestScope(AttributeConst.FLUSH, flush);
            removeSessionScope(AttributeConst.FLUSH);
        }

        //一覧画面を表示
        forward(ForwardConst.FW_REA_INDEX);
    }
    /**
     * 新規登録画面を表示する
     * @throws ServletException
     * @throws IOException
     */
    public void entryNew() throws ServletException, IOException {

        putRequestScope(AttributeConst.TOKEN, getTokenId()); //CSRF対策用トークン

        //リアクション情報の空インスタンスに、リアクションの日付＝今日の日付を設定する
        ReactionView rv = new ReactionView();
        rv.setReactionDate(LocalDate.now());
        putRequestScope(AttributeConst.REACTION, rv); //日付のみ設定済みのリアクションインスタンス

        //新規登録画面を表示
        forward(ForwardConst.FW_REA_NEW);

    }
    /**
     * 新規登録を行う
     * @throws ServletException
     * @throws IOException
     */
    public void create() throws ServletException, IOException {

        //CSRF対策 tokenのチェック
        if (checkToken()) {

            //リアクションの日付が入力されていなければ、今日の日付を設定
            LocalDate day = null;
            if (getRequestParam(AttributeConst.REA_DATE) == null
                    || getRequestParam(AttributeConst.REA_DATE).equals("")) {
                day = LocalDate.now();
            } else {
                day = LocalDate.parse(getRequestParam(AttributeConst.REA_DATE));
            }

            //セッションからログイン中の従業員情報を取得
            EmployeeView ev = (EmployeeView) getSessionScope(AttributeConst.LOGIN_EMP);

            //パラメータの値をもとにリアクション情報のインスタンスを作成する
            ReactionView rv = new ReactionView(
                    null,
                    ev, //ログインしている従業員を、リアクション作成者として登録する
                    getRequestParam(AttributeConst.REA_TITLE),
                    day,
                    getRequestParam(AttributeConst.REA_CONTENT),
                    getRequestParam(AttributeConst.REA_STAMP),
                    null,
                    null);

            //リアクション情報登録
            List<String> errors = service.create(rv);

            if (errors.size() > 0) {
                //登録中にエラーがあった場合

                putRequestScope(AttributeConst.TOKEN, getTokenId()); //CSRF対策用トークン
                putRequestScope(AttributeConst.REACTION, rv);//入力されたリアクション情報
                putRequestScope(AttributeConst.ERR, errors);//エラーのリスト

                //新規登録画面を再表示
                forward(ForwardConst.FW_REA_NEW);

            } else {
                //登録中にエラーがなかった場合

                //セッションに登録完了のフラッシュメッセージを設定
                putSessionScope(AttributeConst.FLUSH, MessageConst.I_REGISTERED.getMessage());

                //一覧画面にリダイレクト
                redirect(ForwardConst.ACT_REA, ForwardConst.CMD_INDEX);
            }
        }
    }
    /**
     * 詳細画面を表示する
     * @throws ServletException
     * @throws IOException
     */
    public void show() throws ServletException, IOException {

        //idを条件にリアクションデータを取得する
        ReactionView rv = service.findOne(toNumber(getRequestParam(AttributeConst.REA_ID)));

        if (rv == null) {
            //該当のリアクションデータが存在しない場合はエラー画面を表示
            forward(ForwardConst.FW_ERR_UNKNOWN);

        } else {

            putRequestScope(AttributeConst.REACTION, rv); //取得したリアクションデータ

            //詳細画面を表示
            forward(ForwardConst.FW_REA_SHOW);
        }
    }
    /**
     * 編集画面を表示する
     * @throws ServletException
     * @throws IOException
     */
    public void edit() throws ServletException, IOException {

        //idを条件にリアクションデータを取得する
        ReactionView rv = service.findOne(toNumber(getRequestParam(AttributeConst.REA_ID)));

        //セッションからログイン中の従業員情報を取得
        EmployeeView ev = (EmployeeView) getSessionScope(AttributeConst.LOGIN_EMP);

        if (rv == null || ev.getId() != rv.getEmployee().getId()) {
            //該当のリアクションデータが存在しない、または
            //ログインしている従業員がリアクションの作成者でない場合はエラー画面を表示
            forward(ForwardConst.FW_ERR_UNKNOWN);

        } else {

            putRequestScope(AttributeConst.TOKEN, getTokenId()); //CSRF対策用トークン
            putRequestScope(AttributeConst.REACTION, rv); //取得したリアクションデータ

            //編集画面を表示
            forward(ForwardConst.FW_REA_EDIT);
        }

    }
    /**
     * 更新を行う
     * @throws ServletException
     * @throws IOException
     */
    public void update() throws ServletException, IOException {

        //CSRF対策 tokenのチェック
        if (checkToken()) {

            //idを条件にリアクションデータを取得する
            ReactionView rv = service.findOne(toNumber(getRequestParam(AttributeConst.REA_ID)));

            //入力されたリアクション内容を設定する
            rv.setReactionDate(toLocalDate(getRequestParam(AttributeConst.REA_DATE)));
            rv.setTitle(getRequestParam(AttributeConst.REA_TITLE));
            rv.setContent(getRequestParam(AttributeConst.REA_CONTENT));

            //リアクションデータを更新する
            List<String> errors = service.update(rv);

            if (errors.size() > 0) {
                //更新中にエラーが発生した場合

                putRequestScope(AttributeConst.TOKEN, getTokenId()); //CSRF対策用トークン
                putRequestScope(AttributeConst.REACTION, rv); //入力されたリアクション情報
                putRequestScope(AttributeConst.ERR, errors); //エラーのリスト

                //編集画面を再表示
                forward(ForwardConst.FW_REA_EDIT);
            } else {
                //更新中にエラーがなかった場合

                //セッションに更新完了のフラッシュメッセージを設定
                putSessionScope(AttributeConst.FLUSH, MessageConst.I_UPDATED.getMessage());

                //一覧画面にリダイレクト
                redirect(ForwardConst.ACT_REP, ForwardConst.CMD_INDEX);

            }
        }
    }
}