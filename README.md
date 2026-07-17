# Auto Flyer v3 (Fabric mod cho Minecraft 1.21.4)

Mod client **Auto Build**: tu dong tim block con thieu gan nhat trong ban ve Litematica dang tai
(chi trong lop dang hien) va bay den ngay phia tren no, de mod print (vd Meteor Litematica Printer)
tu dong dat block. Ban v3 nay chi con dung 1 chuc nang duy nhat nay (bo het che do toa do thu cong / ziczac
cua cac ban truoc).

## Tinh nang
- 1 nut nho **"Auto Build"** o goc tren ben phai man hinh (khi khong mo GUI nao) -> bam vao mo man hinh cai dat.
- Nut **"Auto Flyer"** o goc tren ben trai menu Pause (bam **ESC** trong luc choi) -> mo thang man hinh cai dat.
- Tich hop **Mod Menu**: vao Mods -> Auto Flyer -> nut cai dat.
- 1 **phim tat** trong **Options -> Controls -> Auto Flyer -> "Open Auto Flyer settings"**: mac dinh chua gan
  phim nao, tu chon phim (vd F6) de mo nhanh man hinh cai dat tu bat cu dau.
- Man hinh cai dat co 2 o: **Do cao bay tren block (y+)** (mac dinh 2) va **Toc do bay**, cong voi nut
  **Start/Pause**.
- Bam **Start**: tim block con thieu gan nguoi choi nhat (chi xet trong pham vi lop Litematica dang hien -
  dung "Render layer range" ban da chinh trong Litematica), bay len ngay phia tren no, dung yen cho mod print
  tu dat block, xong tu dong bay sang block tiep theo, lap lai cho den khi het.
- **Khong bao gio pha block** - mod chi di chuyen, khong dung vao inventory/dat/pha gi ca, nen khong xung dot
  voi mod print ban dang dung.
- **Ne vat can**: neu duong bay thang bi tuong chan, tu dong bay vong len tren cao roi ha xuong thay vi dam
  xuyen qua vat can (hop voi xay map art 3D); neu duong thang khong bi chan thi bay thang luon cho nhanh.
  Neu ket o 1 cho qua lau, tu chuyen sang block khac roi quay lai sau.

## Yeu cau
Can 2 mod sau da duoc cai o client (chi de DOC du lieu, da bundle san dung phien ban trong `libs/`):
- **Litematica** (fabric-1.21.4-0.21.7, ban sakura-ryoko fork)
- **malilib** (fabric-1.21.4-0.23.5)

Neu ban cap nhat len ban Litematica/malilib khac, can thay file trong `libs/` va sua duong dan trong
`build.gradle` (phan `dependencies`) cho khop ten file moi.

Khong bat buoc phai cai Litematica de mod nay load duoc (mod van chay binh thuong, chi khi bam Start ma
khong co Litematica thi se bao "khong tim thay mod Litematica" trong chat va khong lam gi ca).

## Cach build bang GitHub Actions (khuyen nghi)
Repo da co san file `.github/workflows/build.yml`, tu dong chay tren nhanh `main`, `master`, `v2`, `v3`.

1. Tao/dung 1 repo GitHub, day (push) toan bo thu muc `autoflyer-mod` len nhanh `v3`:
   ```bash
   cd autoflyer-mod
   git checkout -b v3
   git add .
   git commit -m "Auto Flyer v3: chi con Auto Build"
   git push -u origin v3
   ```
2. Vao tab **Actions** cua repo tren GitHub, workflow "Build Fabric mod" se tu chay (hoac bam **Run workflow**).
3. Sau khi chay xong, vao lan chay do -> muc **Artifacts** o cuoi trang -> tai `autoflyer-mod.zip` ve, giai nen
   ra se co file `.jar`.

## Cach build tai cho (local)
Can cai **JDK 21** va co ket noi Internet.

```bash
cd autoflyer-mod
gradle wrapper --gradle-version 9.5.1   # chi can chay 1 lan de tao gradlew
./gradlew build
```

File jar ket qua nam o `build/libs/autoflyer-3.0.0.jar`.

## Cai dat
1. Cai **Fabric Loader** cho Minecraft 1.21.4.
2. Cai **Fabric API** (bat buoc), **Litematica** + **malilib** (de dung Auto Build), **Mod Menu** (tuy chon).
3. Bo file `autoflyer-3.0.0.jar` vua build vao thu muc `mods`.

## Ghi chu ky thuat
- Day la mod **client-side**: chi can cai o may client, khong can cai tren server.
- Mod dieu khien van toc (velocity) cua nhan vat de bay - server cua ban da cho phep bay tu do (giong Creative)
  nen phan lon se khong bi anti-cheat chan, nhung neu server co plugin chong hack rieng thi nen kiem tra truoc.
- Cac phien ban thu vien (`fabric_version`, `modmenu_version`, `yarn_mappings`, `loom`, `gradle`...) trong
  `gradle.properties`/`build.gradle` co the can cap nhat theo thoi gian.
