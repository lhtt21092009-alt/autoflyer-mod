# Auto Flyer v4 (Fabric mod cho Minecraft 1.21.4)

Mod client co 2 chuc nang:
- **Auto Fly**: bay ziczac (kieu cat co) qua lai giua 2 vi tri toa do ban tu chon.
- **Auto Build**: tu dong tim block con thieu gan nhat trong ban ve Litematica dang tai (chi trong
  lop dang hien) va bay den ngay phia tren no, de mod print (vd Meteor Litematica Printer) tu dat.

Chi 1 trong 2 chuc nang chay tai 1 thoi diem (bat cai nay se tu tat cai kia, tranh xung dot dieu khien
van toc nguoi choi).

## Cach mo / dieu khien
- **Nut o goc tren ben phai man hinh** (khi khong mo GUI nao) -> mo GUI day du.
- **Nut "Auto Flyer" trong menu Pause** (bam ESC) -> mo GUI day du.
- **Mod Menu**: Mods -> Auto Flyer -> nut cai dat -> mo GUI day du.
- **3 phim tat** trong **Options -> Controls -> muc "Auto Flyer"** (mac dinh CHUA gan phim nao, tu chon):
  - **Open Auto Flyer menu**: mo GUI day du (giong cac cach tren).
  - **Toggle Auto Build**: bat/tat Auto Build ngay lap tuc, khong can mo GUI.
  - **Toggle Auto Fly**: bat/tat Auto Fly ngay lap tuc, khong can mo GUI.

## GUI day du co 3 nut
1. **Auto Build (Litematica)** -> man hinh cai dat Auto Build (do cao bay tren block, toc do, Start/Pause).
2. **Auto Fly (thu cong)** -> man hinh cai dat Auto Fly (Vi tri 1, Vi tri 2, buoc ziczac, Start/Pause).
3. **Tro ve** -> dong GUI, quay lai noi da mo no (game hoac Pause menu).

## Auto Build hoat dong the nao
- Chi xet block trong **pham vi lop (layer) Litematica dang hien** (dung "Render layer range" ban chinh
  trong Litematica).
- Tim block con thieu **gan nguoi choi nhat**, bay len ngay phia tren no (y + do cao ban dat, mac dinh y+2),
  dung yen cho mod print tu dat, xong tu bay sang block tiep theo.
- **Khong bao gio pha block** - chi di chuyen, khong dung vao inventory hay dat/pha gi, nen khong xung dot
  voi mod print ban dang dung.
- **Ne vat can thong minh**: neu duong bay thang bi chan, do tang dan tung 2 block 1 de tim do cao THAP
  NHAT du de bay vong qua, thay vi luon bay len dinh cao nhat cong trinh (nhanh hon nhieu voi vat can nho).
- **Chong ket nhieu tang**: phat hien khong nhuc nhich duoc trong 1.2 giay -> nhun len tuc thi + thu do cao
  khac (toi da 3 lan) -> neu van khong duoc thi bo qua block do, chuyen sang block khac; ngoai ra co
  "watchdog" tong thoi gian toi da 30 giay / 1 block, qua thi tu dong bo qua du ly do gi.
- **Am thanh bao hieu** khi xay xong het (khong con block nao thieu trong lop dang hien).

## Yeu cau
Can 2 mod sau da duoc cai o client (chi de DOC du lieu, da bundle san dung phien ban trong `libs/`):
- **Litematica** (fabric-1.21.4-0.21.7, ban sakura-ryoko fork)
- **malilib** (fabric-1.21.4-0.23.5)

Khong bat buoc phai cai Litematica de mod nay load duoc (Auto Fly van chay binh thuong khong can
Litematica). Neu ban cap nhat len ban Litematica/malilib khac, can thay file trong `libs/` va sua duong
dan trong `build.gradle` (phan `dependencies`) cho khop ten file moi.

## Cach build bang GitHub Actions (khuyen nghi)
Repo da co san `.github/workflows/build.yml`, tu dong chay tren nhanh `main`, `master`, `v2`, `v3`, `v4`.

```bash
cd autoflyer-mod
git checkout -b v4
git add .
git commit -m "Auto Flyer v4: them lai Auto Fly, 3 phim tat, sua ket, am thanh done"
git push -u origin v4
```

Vao tab **Actions** cua repo tren GitHub, workflow se tu chay (hoac bam **Run workflow**). Sau khi chay
xong, vao lan chay do -> muc **Artifacts** -> tai `autoflyer-mod.zip` ve, giai nen ra co file `.jar`.

## Cach build tai cho (local)
Can cai **JDK 21** va co ket noi Internet.

```bash
cd autoflyer-mod
gradle wrapper --gradle-version 9.5.1   # chi can chay 1 lan de tao gradlew
./gradlew build
```

File jar ket qua nam o `build/libs/autoflyer-4.0.0.jar`.

## Cai dat
1. Cai **Fabric Loader** cho Minecraft 1.21.4.
2. Cai **Fabric API** (bat buoc), **Litematica** + **malilib** (de dung Auto Build), **Mod Menu** (tuy chon).
3. Bo file `autoflyer-4.0.0.jar` vua build vao thu muc `mods`.

## Ghi chu ky thuat
- Day la mod **client-side**: chi can cai o may client, khong can cai tren server.
- Mod dieu khien van toc (velocity) cua nhan vat de bay - server cua ban da cho phep bay tu do (giong
  Creative) nen phan lon se khong bi anti-cheat chan.
- Cac phien ban thu vien (`fabric_version`, `modmenu_version`, `yarn_mappings`, `loom`, `gradle`...) co the
  can cap nhat theo thoi gian.
