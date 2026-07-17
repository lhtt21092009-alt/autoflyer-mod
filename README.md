# Auto Flyer (Fabric mod cho Minecraft 1.21.4)

Mod client bay tu dong qua lai giua 2 toa do da luu san.

## Tinh nang
- 1 nut nho **"Cai dat"** o goc tren ben phai man hinh (khi khong mo GUI nao) -> bam vao se mo man hinh cai dat.
- Mod cung tich hop **Mod Menu**: vao Mods -> Auto Flyer -> nut cai dat de mo man hinh nay.
- Nut **"Auto Flyer"** o goc tren ben trai menu Pause (bam **ESC** trong luc choi) -> bam vao mo thang man hinh cai dat.
- 1 **phim tat** trong **Options -> Controls -> Auto Flyer -> "Open Auto Flyer settings"**: mac dinh chua gan phim nao,
  ban tu chon 1 phim (vd F6) de mo nhanh man hinh cai dat tu bat cu dau trong game, khong can mo Pause hay bam nut.
- **2 che do bay**, chuyen doi bang nut "Che do" o dau man hinh cai dat:
  - **Thu cong**: nhu mo ta o tren (2 vi tri + ziczac).
  - **Litematica (tu dong)**: doc truc tiep ban ve dang tai trong mod Litematica, tu dong tim **block con thieu
    gan nguoi choi nhat** (chi trong pham vi **lop dang hien** cua Litematica, dung config "Render layer range"
    ban da chinh trong Litematica), bay len ngay phia tren no (y + do cao ban dat, mac dinh y+2) roi **dung yen**
    cho mod print (vd Meteor Litematica Printer) tu dat block, xong tu dong bay sang block tiep theo, lap lai
    cho den khi het block can dat trong lop dang hien. Mod **khong bao gio pha block**, chi di chuyen.
    De tranh bay xuyen tuong khi xay map art 3D, neu duong bay thang bi vuong, mod se tu dong bay vong len tren
    cao roi ha xuong thay vi dam thang qua vat can; neu ket o 1 cho qua lau se tu chuyen sang block khac.

### Yeu cau rieng cho che do Litematica
Che do nay can 2 mod sau da duoc cai (chi de DOC du lieu, khong dung lai jar cua ban - da bundle san trong
`libs/` cua project nay dung phien ban ban dang choi):
- **Litematica** (fabric-1.21.4-0.21.7 ban sakura-ryoko fork)
- **malilib** (fabric-1.21.4-0.23.5)

Neu ban cap nhat len ban Litematica/malilib khac, co the can thay file trong `libs/` va sua lai duong dan trong
`build.gradle` (phan `dependencies`) cho khop ten file moi.
- Man hinh cai dat co:
  - **Vi tri 1**: 3 o nhap X / Y / Z, nut **Set here** (dien toa do hien tai cua ban vao), nut **Clear** (xoa trong).
  - **Vi tri 2**: tuong tu Vi tri 1.
  - O **Buoc ziczac (block)**: khoang cach dich ngang sau moi hang bay (mac dinh 2 block).
  - Nut **Start / Pause** o duoi cung:
    - Bam **Start**: Vi tri 1 va Vi tri 2 duoc xem la 2 goc doi dien cua 1 hinh chu nhat. Nhan vat se bay theo
      kieu **ziczac (giong may cat co)**: bay thang het 1 canh, dich ngang sang 1 khoang (o "Buoc ziczac"), roi
      bay nguoc lai theo huong doi dien - lap lai cho toi khi quet het ca hinh chu nhat, sau do quay ve Vi tri 1
      va lap lai toan bo chu trinh lien tuc. Nut doi thanh **Pause**.
    - Bam **Pause**: dung lai tai cho, nut doi lai thanh **Start**.
- Toa do duoc luu vao `config/autoflyer.json`, giu lai giua cac lan choi.
- Yeu cau server da cho phep bay tu do (flight giong Creative) nhu ban mo ta - mod chi dieu khien van toc nhan vat, khong gui lenh /tp.

## Cach build bang GitHub Actions (khuyen nghi, khong can cai gi tren may ban)
Repo da co san file `.github/workflows/build.yml`. Chi can:

1. Tao 1 repo moi tren GitHub, day (push) toan bo thu muc `autoflyer-mod` nay len:
   ```bash
   cd autoflyer-mod
   git init
   git add .
   git commit -m "Auto Flyer mod"
   git branch -M main
   git remote add origin https://github.com/<ten-ban>/<ten-repo>.git
   git push -u origin main
   ```
2. Vao tab **Actions** cua repo tren GitHub, workflow "Build Fabric mod" se tu chay.
3. Sau khi chay xong (thuong 2-4 phut), vao lan chay do -> phan **Artifacts** o cuoi trang -> tai file
   `autoflyer-mod.zip` ve, giai nen ra se co file `.jar` trong do.

Ban cung co the bam nut **Run workflow** (workflow_dispatch) trong tab Actions de chay build thu cong bat cu luc nao
ma khong can push code moi.

## Cach build tai cho (local)
Can cai **JDK 21** va co ket noi Internet (de Gradle tai Fabric Loom, Minecraft, Yarn mappings...). Vi source nay
chua kem san Gradle wrapper binary, ban chay:

```bash
cd autoflyer-mod
gradle wrapper --gradle-version 8.10   # chi can chay 1 lan de tao gradlew
./gradlew build
```

File jar ket qua nam o `build/libs/autoflyer-1.0.0.jar`.

## Cai dat
1. Cai **Fabric Loader** cho Minecraft 1.21.4.
2. Tai **Fabric API** (bat buoc) va **Mod Menu** (tuy chon, de co nut cai dat trong Mods menu) roi bo vao thu muc `mods`.
3. Bo file `autoflyer-1.0.0.jar` vua build vao thu muc `mods`.

## Ghi chu ky thuat
- Day la mod **client-side**: chi can cai o may client, khong can cai tren server.
- Vi mod dieu khien van toc (velocity) cua nhan vat de bay, mot so anti-cheat plugin tren server co the phat hien
  chuyen dong "qua deu/qua thang" bat thuong. Ban noi server da co san co che bay giong Creative nen phan lon
  se khong bi chan, nhung neu server co plugin chong hack rieng thi ban nen kiem tra truoc.
- Cac phien ban thu vien (`fabric_version`, `modmenu_version`, `yarn_mappings`...) trong `gradle.properties` co the
  can cap nhat len ban moi nhat tai thoi diem ban build, vi cac ban Fabric API / Yarn moi ra lien tuc.
