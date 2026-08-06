# IMA-for-kotlin

更新日：Feb 28, 2025

Kotlin / Android で作った在庫管理アプリです。
C# / WinForms で作った [inventManagementApp](https://github.com/nemuinug/inventManagementApp) と
同じ題材を、Android 向けに実装し直したものです。
論理削除・数量・コメント・画像の BLOB 保存という設計方針を引き継ぎつつ、項目は見直しています。


## 何ができるか

- 在庫アイテムの登録・一覧表示・詳細表示
- 数量の増減とコメントの記録
- 各アイテムへの画像の添付（未設定時はデフォルト画像）
- チェック状態の切り替え
- 登録日時の自動記録
- 削除は論理削除（データは残したまま一覧から除外）

## 技術スタック

| 項目 | 内容 |
|---|---|
| 言語 | Kotlin |
| UI | Android View（RecyclerView + カスタムアダプタ） |
| DB | SQLite（`SQLiteOpenHelper` を直接利用） |
| compileSdk | 35 |
| minSdk | 24 |

## データ構造

```sql
CREATE TABLE IF NOT EXISTS Inventory (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    name        TEXT    NOT NULL,
    quantity    INTEGER NOT NULL DEFAULT 1,
    isChecked   INTEGER NOT NULL DEFAULT 0,
    createdTime TEXT    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    comment     TEXT,
    image       BLOB,
    isDeleted   INTEGER NOT NULL DEFAULT 0
);
```

### 設計上の判断

Boolean を INTEGER で持つ理由 — SQLite に Boolean 型がないため、
`intToBoolean()` / `booleanToInt()` の変換をヘルパー側に閉じ込め、
アプリのコードからは `Boolean` として扱えるようにしています。

画像を BLOB で持つ理由 — 端末のファイルパスに依存すると参照が壊れるため、
`Bitmap` をバイト列に変換して DB に格納しています。

論理削除は、[inventManagementApp](https://github.com/nemuinug/inventManagementApp) の
レビューで指摘を受けて採用した方式を、こちらでも踏襲しています。

## 開発の進め方

このリポジトリでは、機能の実装と並行して Issue 駆動の開発フローを実践しています。

| 項目 | 数 |
|---|---|
| Issue | 37件（すべてクローズ済み） |
| Pull Request | 39件 |
| ブランチ | 39本 |

### Issue の立て方

1つの機能について、UI の追加と処理の実装で Issue を分けています。

| Issue | 内容 |
|---|---|
| #26 ✨サムネイルの追加 | 画面に要素を置く |
| #27 🖥️サムネイルの実装 | その要素を動かす |
| #28 ✨IDラベルの追加 | 〃 |
| #29 🖥️IDラベルの実装 | 〃 |
| #36 ✨保存ボタンの追加 | 〃 |
| #37 🖥️保存ボタンの実装 | 〃 |

分けた理由は、画面を先に組んでから中身を実装する順序で進めたかったためです。
Issue 番号に対応する feature ブランチ（主に `feature/issue-<番号>`）を切り、`develop` へ Pull Request を出しています。

### コードレビュー

経験者の方にレビューをお願いし、指摘に対応しました。

| 指摘 | 対応 |
|---|---|
| 意味を持つ値はマジックナンバーにせず定数として宣言する | `0` などの直値を定数化 |
| 数量の上限・下限も定数として名前を付ける | 閾値を定数化 |
| デフォルト値であることが分かる名前にする | 命名を変更 |
| 真偽を判断する箇所は `Default` より `True` / `False` のほうが分かりやすい | 命名を変更 |
| 無駄な空行を削除する | 整形 |

## 構成

| ファイル | 役割 |
|---|---|
| `databaseHelper.kt` | SQLite の定義と CRUD（195行）。テーブル作成、登録、取得、更新、論理削除、Bitmap↔ByteArray 変換 |
| `MainActivity.kt` | 一覧画面 |
| `SubActivity.kt` | 詳細・編集画面 |
| `ItemAdapter.kt` | RecyclerView のアダプタ |
| `item.kt` | データクラス |
| `class.kt` / `design.kt` | 補助 |

## ビルドと実行

```
Android Studio で開く → Gradle Sync → 実機またはエミュレータで Run
```

必要環境: Android Studio、JDK 17、Android SDK 35

## C# 版との比較

同じ題材を2つのプラットフォームで実装した際の差分です。

| | このリポジトリ | [inventManagementApp](https://github.com/nemuinug/inventManagementApp) |
|---|---|---|
| 言語 | Kotlin | C# |
| プラットフォーム | Android | Windows デスクトップ |
| UI | RecyclerView + アダプタ | Windows Forms |
| 画面遷移 | Activity と Intent | Form の呼び出し |
| DB アクセス | `SQLiteOpenHelper` | `SQLiteCommand` |

### テーブル定義の対応

スキーマそのものは同一ではありません。再実装にあたって項目を見直しています。

| 概念 | このリポジトリ（`Inventory`） | C# 版（`images`） |
|---|---|---|
| 主キー | `id` | `id` |
| 名称 | `name` | なし |
| 数量 | `quantity`（INTEGER） | `Quantity`（TEXT） |
| チェック状態 | `isChecked` | なし |
| 日時 | `createdTime`（登録時に自動記録） | `record_day`（手入力の記録日） |
| コメント | `comment` | `comment` |
| 画像 | `image`（BLOB） | `image_data`（BLOB） |
| 論理削除 | `isDeleted` | `is_deleted_id` |

共通しているのは、論理削除・数量・コメント・画像を BLOB で持つという考え方の部分です。
Kotlin 版では名称とチェック状態を追加し、数量は TEXT ではなく INTEGER にしました。
日時も、C# 版の手入力から登録時の自動記録に変えています。

UI と画面遷移は考え方が根本的に異なりました。
Android では画面を Activity として独立させ Intent で値を渡す必要があり、
デスクトップのようにフォームを直接呼び出す構造にはなりません。
同じ題材を2度書くことで、その違いを構造として把握できたことが目的でした。

## 現状

学習目的で作ったもので、以下は未対応です。

- 検索・並び替え
- 画像の複数枚添付
- テストコード（雛形のみ）
