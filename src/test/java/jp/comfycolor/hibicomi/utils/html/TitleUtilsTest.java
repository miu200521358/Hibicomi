package jp.comfycolor.hibicomi.utils.html;

import static org.junit.Assert.*;

import org.junit.Test;

import jp.comfycolor.hibicomi.utils.text.BookTextUtils;

public class TitleUtilsTest {

	@Test
	public void testFormatTitle() {
		assertEquals(BookTextUtils.formatTitle("ギルティ ～鳴かぬ蛍が身を焦がす～ 分冊版"), "ギルティ ～鳴かぬ蛍が身を焦がす～");
		assertEquals(BookTextUtils.formatTitle("隠し部屋 分冊版"), "隠し部屋");
		assertEquals(BookTextUtils.formatTitle("終末のハーレム セミカラー版"), "終末のハーレム");
		assertEquals(BookTextUtils.formatTitle("トリコ モノクロ版"), "トリコ");
		assertEquals(BookTextUtils.formatTitle("NARUTO―ナルト― カラー版"), "NARUTO―ナルト―");
		assertEquals(BookTextUtils.formatTitle("ＮＡＲＵＴＯ―ナルト― カラー版"), "NARUTO―ナルト―" );
		assertEquals(BookTextUtils.formatTitle("バクマン。 カラー版"), "バクマン。");
		assertEquals(BookTextUtils.formatTitle("恥辱の檻～悦楽調教プログラム～ 分冊版"), "恥辱の檻～悦楽調教プログラム～");
		assertEquals(BookTextUtils.formatTitle("鬼親～2015年うさぎケージ監禁男児虐待事件～(単話版)"), "鬼親～2015年うさぎケージ監禁男児虐待事件～");
		assertEquals(BookTextUtils.formatTitle("終末のハーレム セミカラー版"), "終末のハーレム");
		assertEquals(BookTextUtils.formatTitle("終末のハーレム　セミカラー版"), "終末のハーレム");
		assertEquals(BookTextUtils.formatTitle("三兄弟、おにいちゃんの恋 【電子限定特典付き】"), "三兄弟、おにいちゃんの恋");
		assertEquals(BookTextUtils.formatTitle("三兄弟、おにいちゃんの恋　【電子限定特典付き】"), "三兄弟、おにいちゃんの恋");
		assertEquals(BookTextUtils.formatTitle("臆病者の恋［コミックス版］"), "臆病者の恋");
		assertEquals(BookTextUtils.formatTitle("おりこう野獣にはあらがえない【電子限定特典つき】"), "おりこう野獣にはあらがえない");
		assertEquals(BookTextUtils.formatTitle("慰めあうだけだもの【電子限定特別ページつき】"), "慰めあうだけだもの");
		assertEquals(BookTextUtils.formatTitle("恋愛不行き届き　【電子限定特典付き】"), "恋愛不行き届き");
		assertEquals(BookTextUtils.formatTitle("愛されすぎてカラダもたない…！～30歳干物女が男子校生の嫁になったら～【フルカラー】"), "愛されすぎてカラダもたない…！～30歳干物女が男子校生の嫁になったら～");
		assertEquals(BookTextUtils.formatTitle("愛されすぎてカラダもたない…!～30歳干物女が男子校生の嫁になったら～【フルカラー】"), "愛されすぎてカラダもたない…！～30歳干物女が男子校生の嫁になったら～");
		assertEquals(BookTextUtils.formatTitle("食糧人類-Starving Anonymous-"), "食糧人類－Starving Anonymous－");
		assertEquals(BookTextUtils.formatTitle("食糧人類－Starving Anonymous－"), "食糧人類－Starving Anonymous－");
		assertEquals(BookTextUtils.formatTitle("ノンケの俺がアダルトグッズにおちるわけがない！？（単話）"), "ノンケの俺がアダルトグッズにおちるわけがない！？");
		assertEquals(BookTextUtils.formatTitle("cresc.それでも俺のものになる Qpa edition【電子限定描き下ろし漫画付き】"), "cresc.それでも俺のものになる Qpa edition");
		assertEquals(BookTextUtils.formatTitle("お前のおクチを塞（ふさ）がないとな？～彼に教わるMの品格～（単話）"), "お前のおクチを塞（ふさ）がないとな？～彼に教わるMの品格～");
		assertEquals(BookTextUtils.formatTitle("［カラー版］ナナとカオル"), "ナナとカオル");
		assertEquals(BookTextUtils.formatTitle("花魁遊戯　夜の蜜に甘く濡れる【完全版】（分冊版）"), "花魁遊戯 夜の蜜に甘く濡れる");
		assertEquals(BookTextUtils.formatTitle("恋降るカラフル～ぜんぶキミとはじめて～（９）"), "恋降るカラフル～ぜんぶキミとはじめて～");
		assertEquals(BookTextUtils.formatTitle("約束のネバーランド 2"), "約束のネバーランド");
		assertEquals(BookTextUtils.formatTitle("イムリ 22【電子特典つき】"), "イムリ");
		assertEquals(BookTextUtils.formatTitle("cresc.それでも俺のものになる Qpa edition【電子限定描き下ろし漫画付き】"), "cresc.それでも俺のものになる Qpa edition");
		assertEquals(BookTextUtils.formatTitle("オフィスのケダモノ〜危険な上司と3ヶ月限定彼女"), "オフィスのケダモノ～危険な上司と3ヶ月限定彼女");
		assertEquals(BookTextUtils.formatTitle("蝶と花の関係性【電子限定特典付き】"), "蝶と花の関係性");
		assertEquals(BookTextUtils.formatTitle("俺が両性なんて認めない！【完全版 （電子限定描き下ろし付）】"), "俺が両性なんて認めない！");
		assertEquals(BookTextUtils.formatTitle("ONE PIECE カラー版 80"), "ONE PIECE");
		assertEquals(BookTextUtils.formatTitle("蝶と花の関係性【短編】"), "蝶と花の関係性");
		assertEquals(BookTextUtils.formatTitle("強性結婚～ガテン肉食男子×インテリ草食女子～【合冊版】８"), "強性結婚～ガテン肉食男子×インテリ草食女子～");
		assertEquals(BookTextUtils.formatTitle("脂肪と言う名の服を着て［完全版］　下巻"), "脂肪と言う名の服を着て");
		assertEquals(BookTextUtils.formatTitle("In These Words＜特別版＞"), "In These Words");
		assertEquals(BookTextUtils.formatTitle("ねこねこハニー（2）＜単行本未収録コミック付＞"), "ねこねこハニー");
		assertEquals(BookTextUtils.formatTitle("転生侯爵令嬢はS系教師に恋をする。【SS付】【イラス..."), "転生侯爵令嬢はS系教師に恋をする。");
		assertEquals(BookTextUtils.formatTitle("星に落ちる絵筆【電子限定特典付き】上"), "星に落ちる絵筆");
		assertEquals(BookTextUtils.formatTitle("愛の在り処に誓え！【電子限定書き下ろしＳＳつき】..."), "愛の在り処に誓え！");
		assertEquals(BookTextUtils.formatTitle("狐の婿取り-神様、さらわれるの巻-【特別版】(イラス..."), "狐の婿取り－神様、さらわれるの巻－");
		assertEquals(BookTextUtils.formatTitle("僕らはまだ氷河期の途中【電子限定おまけ付き】"), "僕らはまだ氷河期の途中");
		assertEquals(BookTextUtils.formatTitle("はだける怪物（上）［小冊子付特装版］"), "はだける怪物");
		assertEquals(BookTextUtils.formatTitle("本好きの下剋上"), "本好きの下剋上");
		assertEquals(BookTextUtils.formatTitle("脂肪と言う名の服を着て［完全版]"), "脂肪と言う名の服を着て");
		assertEquals(BookTextUtils.formatTitle("最愛キャラ（死亡フラグ付）の嫁になれたので命かけて守ります【初回限定SS・電子限定SS付】【イラスト付】"), "最愛キャラ（死亡フラグ付）の嫁になれたので命かけて守ります");
		assertEquals(BookTextUtils.formatTitle("らめぇ綿棒でもおっきすぎる…！～目覚めたら1/10の私～"), "らめぇ綿棒でもおっきすぎる…！～目覚めたら1/10の私～");
		assertEquals(BookTextUtils.formatTitle("好きなのにカラダだけ 第1巻"), "好きなのにカラダだけ");
	}

	@Test
	public void testFormatTitleNumber() {
		assertEquals(BookTextUtils.formatTitleNumber("恋降るカラフル～ぜんぶキミとはじめて～（９）"), "９");
		assertEquals(BookTextUtils.formatTitleNumber("約束のネバーランド 2"), "2");
		assertEquals(BookTextUtils.formatTitleNumber("イムリ 22【電子特典つき】"), "22");
		assertEquals(BookTextUtils.formatTitleNumber("ONE PIECE カラー版 80"), "80");
		assertEquals(BookTextUtils.formatTitleNumber("強性結婚～ガテン肉食男子×インテリ草食女子～【合冊版】８"), "８");
		assertEquals(BookTextUtils.formatTitleNumber("ねこねこハニー（2）＜単行本未収録コミック付＞"), "2");
		assertEquals(BookTextUtils.formatTitleNumber("星に落ちる絵筆【電子限定特典付き】上"), "上");
		assertEquals(BookTextUtils.formatTitleNumber("はだける怪物（上）［小冊子付特装版］"), "上");
		assertEquals(BookTextUtils.formatTitleNumber("本好きの下剋上"), null);
		assertEquals(BookTextUtils.formatTitleNumber("好きなのにカラダだけ 第1巻"), "1");
	}

}
