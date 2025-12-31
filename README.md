Dock Icon Mod (NeoForge 1.21.x)
=======

This mod is client-only. On macOS it replaces the Minecraft Dock icon at startup
when a custom image is present.

Usage
=====
- Place `dock_icon.png` in `.minecraft/config/` (macOS example: `~/Library/Application Support/minecraft/config/dock_icon.png`).
- Or set a custom path in `.minecraft/config/dockicon-client.toml` (`iconPath`), relative to the config folder or absolute (`~` is supported).
- Only PNG is supported.
- Recommended image: square PNG, 512x512 or 1024x1024, with transparency.
- Reload in-game with `/dockicon reload` (client command) or F3+T.
- If Taskbar is reported as unsupported, force headful AWT in your launcher:
  `-Djava.awt.headless=false`
- If Taskbar is unsupported, enable the Apple EAWT fallback with a JVM arg:
  `--add-exports=java.desktop/com.apple.eawt=ALL-UNNAMED`
- On macOS, a JVM-args warning screen is shown on startup. Use "don't show again" to hide it.

使い方（日本語）
=====
- `dock_icon.png` を `.minecraft/config/` に置いてください（macOS例: `~/Library/Application Support/minecraft/config/dock_icon.png`）。
- もしくは `.minecraft/config/dockicon-client.toml` の `iconPath` で任意の場所を指定できます（相対パスは config 基準、`~` も可）。
- PNG のみ対応です。
- 推奨サイズ: 正方形 PNG、512x512 または 1024x1024（透過あり）。
- 変更後は `/dockicon reload`（クライアントコマンド）か F3+T で再読み込みできます。
- Taskbar が使えない場合は、ランチャーの JVM 引数に以下を追加してください:
  `-Djava.awt.headless=false`
- Taskbar が使えない場合は Apple EAWT のフォールバックを有効化してください:
  `--add-exports=java.desktop/com.apple.eawt=ALL-UNNAMED`
- macOS では起動時に JVM 引数の注意画面が表示されます。「このメッセージを二度と表示しない」で非表示にできます。

Run client (dev)
=====
1. Put the icon at `runs/client/config/dock_icon.png` in this project (or set `iconPath` in `runs/client/config/dockicon-client.toml`).
2. Run `./gradlew runClient`.
3. When Minecraft starts, the Dock icon should update.
4. To reload after editing the PNG, use `/dockicon reload` or F3+T.

Prism Launcher (macOS)
=====
1. Open the instance settings for your pack.
2. Add these JVM arguments:
   - `-Djava.awt.headless=false`
   - `--add-exports=java.desktop/com.apple.eawt=ALL-UNNAMED`
3. Place the icon at `config/dock_icon.png` inside that instance folder (or set `iconPath` in `config/dockicon-client.toml`).
4. Launch the instance. The Dock icon should update shortly after the window opens.

Prism Launcher（macOS）
=====
1. 対象インスタンスの設定を開きます。
2. JVM 引数に以下を追加します:
   - `-Djava.awt.headless=false`
   - `--add-exports=java.desktop/com.apple.eawt=ALL-UNNAMED`
3. インスタンスフォルダの `config/dock_icon.png` に PNG を置きます
   （または `config/dockicon-client.toml` の `iconPath` を設定）。
4. 起動後、ウィンドウが開いて少ししたら Dock アイコンが更新されます。
