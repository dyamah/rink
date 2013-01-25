rink プロジェクト概要

  rink プロジェクトはIWPT2003で提案した決定的英語依存構造解析({{http://cl.aist-nara.ac.jp/papers/2003/hiroya-y/IWPT-2003.pdf}})のPure Java実装です。

*動作環境

  *JRE 1.6以降
  *libsvm.jar   (version 3.1) SVMライブラリ
  *commons-cli  (version 1.2) コマンドライン解析ライブラリ

*使用方法

**LIBSVMを利用した学習ツール

    IWPT2003 と同じ設定で学習モデルを作成
    (Unix系を想定）
  　 % java -Xmx 1024m -cp rink.jar:libsvm.jar:commons-cli.jar jp.gr.java_conf.dyama.rink.tools.DependencyLearner -i samples/train.txt -o model.svm -g -l SVM　-m 300


**学習モデルを利用した解析方法

    % java -Xmx 512m -cp rink.jar:commons-cli.jar jp.gr.java_conf.dyama.rink.tools.DependencyParser -i samples/train.txt -m model.svm -v

*解析精度

*解析速度

*今後の課題

*謝辞


以上

2013/01/31 Hiroyasu Yamada (dyama.h@gmail.com)