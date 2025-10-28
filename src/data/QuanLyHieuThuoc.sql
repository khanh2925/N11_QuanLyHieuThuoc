/* ===========================================================
   RESET DATABASE
   =========================================================== */
USE master;
GO
DROP DATABASE QuanLyHieuThuoc;
GO
CREATE DATABASE QuanLyHieuThuoc;
GO
USE QuanLyHieuThuoc;
GO

/* ===========================================================
   1) SCHEMA (18 BẢNG)
   =========================================================== */

-- 1) DonViTinh
CREATE TABLE DonViTinh (
    MaDonViTinh CHAR(7) NOT NULL PRIMARY KEY, -- DVT-xxx
    TenDonViTinh NVARCHAR(50) NOT NULL CHECK (LEN(LTRIM(RTRIM(TenDonViTinh))) > 0),
    MoTa NVARCHAR(200) NULL,
    CONSTRAINT CK_DVT_Ma CHECK (MaDonViTinh LIKE 'DVT-[0-9][0-9][0-9]' AND LEN(MaDonViTinh)=7) 
);

-- 2) SanPham (enum loại + kiểm tra giá theo bậc)
CREATE TABLE SanPham (
    MaSanPham CHAR(8) NOT NULL PRIMARY KEY, -- SPxxxxxx
    TenSanPham NVARCHAR(100) NOT NULL CHECK (LEN(LTRIM(RTRIM(TenSanPham))) > 0), 
    LoaiSanPham NVARCHAR(50) NOT NULL
        CHECK (LoaiSanPham IN (N'THUOC', N'VAT_TU', N'THUC_PHAM_BO_SUNG', N'THIET_BI_Y_TE')),
    SoDangKy NVARCHAR(20) NULL,  
    DuongDung NVARCHAR(10) NULL
        CHECK (DuongDung IN (N'UONG', N'TIEM', N'NHO', N'BOI', N'HIT', N'NGAM', N'DAT', N'DAN')),
    GiaNhap FLOAT NOT NULL CHECK (GiaNhap > 0),
    GiaBan  FLOAT NOT NULL CHECK (GiaBan > 0),
    HinhAnh NVARCHAR(255) NULL,
    KeBanSanPham NVARCHAR(100) NULL,
    HoatDong BIT NOT NULL DEFAULT 1,
    
    CONSTRAINT CK_SP_Ma CHECK (MaSanPham LIKE 'SP[0-9][0-9][0-9][0-9][0-9][0-9]' AND LEN(MaSanPham)=8),
    CONSTRAINT UQ_SP_SoDangKy UNIQUE (SoDangKy),
    
    CONSTRAINT CK_SP_GiaBan_BacLoiNhuan CHECK (
        (GiaNhap < 10000  AND GiaBan >= GiaNhap * 1.5) OR
        (GiaNhap >= 10000 AND GiaNhap < 50000  AND GiaBan >= GiaNhap * 1.3) OR
        (GiaNhap >= 50000 AND GiaNhap < 200000 AND GiaBan >= GiaNhap * 1.2) OR
        (GiaNhap >= 200000 AND GiaBan >= GiaNhap * 1.1)
    )
);

-- 3) QuyCachDongGoi
CREATE TABLE QuyCachDongGoi (
    MaQuyCach INT IDENTITY(1,1) PRIMARY KEY,
    MaSanPham CHAR(8) NOT NULL,
    MaDonViTinh CHAR(7) NOT NULL,
    HeSoQuyDoi INT NOT NULL CHECK (HeSoQuyDoi > 0),
    TiLeGiam FLOAT NOT NULL DEFAULT 0 CHECK (TiLeGiam >= 0 AND TiLeGiam <= 1), 
    DonViGoc BIT NOT NULL DEFAULT 0,
    CONSTRAINT FK_QC_SanPham FOREIGN KEY (MaSanPham) REFERENCES SanPham(MaSanPham),
    CONSTRAINT FK_QC_DVT FOREIGN KEY (MaDonViTinh) REFERENCES DonViTinh(MaDonViTinh),
    CONSTRAINT UQ_QC_SP_DVT UNIQUE (MaSanPham, MaDonViTinh) 
);

-- 4) LoSanPham
CREATE TABLE LoSanPham (
    MaLo CHAR(9) NOT NULL PRIMARY KEY,    -- LO-xxxxxx
    HanSuDung DATE NOT NULL,
    SoLuongTon INT NOT NULL DEFAULT 0 CHECK (SoLuongTon >= 0),
    MaSanPham CHAR(8) NOT NULL,
    CONSTRAINT FK_Lo_SP FOREIGN KEY (MaSanPham) REFERENCES SanPham(MaSanPham),
    CONSTRAINT CK_Lo_Ma CHECK (MaLo LIKE 'LO-[0-9][0-9][0-9][0-9][0-9][0-9]' AND LEN(MaLo)=9) 
);

-- 5) TaiKhoan
CREATE TABLE TaiKhoan (
    MaTaiKhoan CHAR(8) NOT NULL PRIMARY KEY, -- TKxxxxxx
    TenDangNhap VARCHAR(30) NOT NULL UNIQUE
        CHECK (LEN(TenDangNhap) BETWEEN 5 AND 30 AND TenDangNhap NOT LIKE '%[^0-9A-Za-z]%'),
    MatKhau NVARCHAR(100) NOT NULL CHECK (LEN(MatKhau) >= 8),
    CONSTRAINT CK_TK_Ma CHECK (MaTaiKhoan LIKE 'TK[0-9][0-9][0-9][0-9][0-9][0-9]' AND LEN(MaTaiKhoan)=8) 
);

-- 6) NhanVien
CREATE TABLE NhanVien (
    MaNhanVien CHAR(12) NOT NULL PRIMARY KEY, -- NVyyyyMMxxxx (12)
    TenNhanVien NVARCHAR(50) NOT NULL CHECK (LEN(LTRIM(RTRIM(TenNhanVien))) > 0),
    GioiTinh BIT NOT NULL CHECK (GioiTinh IN (0,1)) DEFAULT 1,
    NgaySinh DATE NOT NULL CHECK (DATEDIFF(YEAR, NgaySinh, GETDATE()) >= 18),
    DiaChi NVARCHAR(100) NULL,
    SoDienThoai CHAR(10) NULL CHECK (LEN(SoDienThoai)=10 AND SoDienThoai LIKE '0[0-9]%'),
    QuanLy BIT NOT NULL CHECK (QuanLy IN (0,1)) DEFAULT 0,
    MaTaiKhoan CHAR(8) NULL,
    CaLamId CHAR(5) NULL CHECK (CaLamId IN ('SANG','CHIEU','TOI')),
    TrangThai BIT NOT NULL DEFAULT 1,
    CONSTRAINT FK_NV_TK FOREIGN KEY (MaTaiKhoan) REFERENCES TaiKhoan(MaTaiKhoan),
    CONSTRAINT CK_NV_Ma CHECK (MaNhanVien LIKE 'NV[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]' AND LEN(MaNhanVien)=12) 
);

-- 7) KhachHang
CREATE TABLE KhachHang (
    MaKhachHang CHAR(7) NOT NULL PRIMARY KEY, -- KH-xxxx
    TenKhachHang NVARCHAR(100) NOT NULL CHECK (LEN(LTRIM(RTRIM(TenKhachHang))) > 0),
	GioiTinh BIT NOT NULL DEFAULT 1 CHECK (GioiTinh IN (0,1)),
    SoDienThoai CHAR(10) NULL CHECK (LEN(SoDienThoai)=10 AND SoDienThoai LIKE '0[0-9]%'),   
    NgaySinh DATE NULL CHECK (NgaySinh IS NULL OR DATEDIFF(YEAR, NgaySinh, GETDATE()) >= 6),
    CONSTRAINT CK_KH_Ma CHECK (MaKhachHang LIKE 'KH-[0-9][0-9][0-9][0-9]' AND LEN(MaKhachHang)=7)
);

-- 8) NhaCungCap
CREATE TABLE NhaCungCap (
    MaNhaCungCap CHAR(7) NOT NULL PRIMARY KEY, -- NCC-xxx
    TenNhaCungCap NVARCHAR(100) NOT NULL CHECK (LEN(LTRIM(RTRIM(TenNhaCungCap))) > 0),
    DiaChi NVARCHAR(200) NULL,
    SoDienThoai CHAR(10) NULL CHECK (LEN(SoDienThoai)=10 AND SoDienThoai LIKE '0[0-9]%'),
    CONSTRAINT CK_NCC_Ma CHECK (MaNhaCungCap LIKE 'NCC-[0-9][0-9][0-9]' AND LEN(MaNhaCungCap)=7)
);

-- 9) PhieuNhap
CREATE TABLE PhieuNhap (
    MaPhieuNhap CHAR(9) NOT NULL PRIMARY KEY, -- PNxxxxxxx
    NgayNhap DATE NOT NULL CHECK (NgayNhap <= CAST(GETDATE() AS DATE)),
    MaNhaCungCap CHAR(7) NOT NULL,
    MaNhanVien CHAR(12) NOT NULL,
    TongTien MONEY NULL CHECK (TongTien >= 0),
    CONSTRAINT FK_PN_NCC FOREIGN KEY (MaNhaCungCap) REFERENCES NhaCungCap(MaNhaCungCap),
    CONSTRAINT FK_PN_NV  FOREIGN KEY (MaNhanVien)    REFERENCES NhanVien(MaNhanVien),
    CONSTRAINT CK_PN_Ma CHECK (MaPhieuNhap LIKE 'PN[0-9][0-9][0-9][0-9][0-9][0-9][0-9]' AND LEN(MaPhieuNhap)=9)
);

-- 10) ChiTietPhieuNhap
CREATE TABLE ChiTietPhieuNhap (
    MaPhieuNhap CHAR(9) NOT NULL,
    MaLo CHAR(9) NOT NULL,
    SoLuongNhap INT NOT NULL CHECK (SoLuongNhap > 0),
    DonGiaNhap DECIMAL(18,2) NOT NULL CHECK (DonGiaNhap > 0),
    ThanhTien AS (CAST(SoLuongNhap AS DECIMAL(18,2)) * DonGiaNhap) PERSISTED,
    CONSTRAINT PK_CTPN PRIMARY KEY (MaPhieuNhap, MaLo),
    CONSTRAINT FK_CTPN_PN FOREIGN KEY (MaPhieuNhap) REFERENCES PhieuNhap(MaPhieuNhap),
    CONSTRAINT FK_CTPN_Lo FOREIGN KEY (MaLo) REFERENCES LoSanPham(MaLo)
);

-- 11) KhuyenMai
CREATE TABLE KhuyenMai (
    MaKM CHAR(16) NOT NULL PRIMARY KEY, -- KM-YYYYMMDD-ssss (16)
    TenKM NVARCHAR(200) NOT NULL CHECK (LEN(LTRIM(RTRIM(TenKM))) > 0),
    NgayBatDau DATE NOT NULL,
    NgayKetThuc DATE NOT NULL,
    TrangThai BIT NOT NULL DEFAULT 1 CHECK (TrangThai IN (0,1)),
    KhuyenMaiHoaDon BIT NOT NULL DEFAULT 0 CHECK (KhuyenMaiHoaDon IN (0,1)),
    
    -- ĐÃ SỬA: Chuyển sang NVARCHAR và CHECK ENUM
    HinhThucKM NVARCHAR(30) NOT NULL 
        CHECK (HinhThucKM IN ('GIAM_GIA_PHAN_TRAM', 'GIAM_GIA_TIEN', 'TANG_THEM')),
        
    GiaTri MONEY NOT NULL CHECK (GiaTri >= 0),
    
    -- ĐÃ SỬA: Chuyển sang DECIMAL NOT NULL DEFAULT 0
    DieuKienApDungHoaDon DECIMAL(18, 2) NOT NULL DEFAULT 0 CHECK (DieuKienApDungHoaDon >= 0),
    
    SoLuongToiThieu INT NOT NULL DEFAULT 0 CHECK (SoLuongToiThieu >= 0),
    SoLuongTangThem INT NOT NULL DEFAULT 0 CHECK (SoLuongTangThem >= 0),
    CONSTRAINT CK_KM_Date CHECK (NgayBatDau <= NgayKetThuc),
    CONSTRAINT CK_KM_Ma CHECK (MaKM LIKE 'KM-[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]-[0-9][0-9][0-9][0-9]' AND LEN(MaKM)=16)
);

-- 12) HoaDon
CREATE TABLE HoaDon (
    MaHoaDon CHAR(16) NOT NULL PRIMARY KEY, -- HD-YYYYMMDD-ssss (16)
    NgayLap DATE NOT NULL CHECK (NgayLap <= CAST(GETDATE() AS DATE)),
    MaNhanVien CHAR(12) NOT NULL,
    MaKhachHang CHAR(7) NOT NULL,
    TongTien DECIMAL(18,2) NULL,
    ThuocTheoDon BIT NOT NULL DEFAULT 0 CHECK (ThuocTheoDon IN (0,1)),
    CONSTRAINT FK_HD_NV FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien),
    CONSTRAINT FK_HD_KH FOREIGN KEY (MaKhachHang) REFERENCES KhachHang(MaKhachHang),
    CONSTRAINT CK_HD_Ma CHECK (MaHoaDon LIKE 'HD-[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]-[0-9][0-9][0-9][0-9]' AND LEN(MaHoaDon)=16)
);

-- 13) ChiTietHoaDon
CREATE TABLE ChiTietHoaDon (
    MaHoaDon CHAR(16) NOT NULL,
    MaSanPham CHAR(8) NOT NULL,
    MaKM CHAR(16) NULL,
    SoLuong INT NOT NULL CHECK (SoLuong > 0),
    GiaBan MONEY NOT NULL CHECK (GiaBan > 0),
    ThanhTien AS (CAST(SoLuong AS DECIMAL(18,2)) * CAST(GiaBan AS DECIMAL(18,2))) PERSISTED,
    CONSTRAINT PK_CTHD PRIMARY KEY (MaHoaDon, MaSanPham),
    CONSTRAINT FK_CTHD_HD FOREIGN KEY (MaHoaDon) REFERENCES HoaDon(MaHoaDon),
    CONSTRAINT FK_CTHD_SP FOREIGN KEY (MaSanPham) REFERENCES SanPham(MaSanPham),
    CONSTRAINT FK_CTHD_KM FOREIGN KEY (MaKM) REFERENCES KhuyenMai(MaKM)
);

-- 14) ChiTietKhuyenMaiSanPham
CREATE TABLE ChiTietKhuyenMaiSanPham (
    MaKM CHAR(16) NOT NULL,
    MaSanPham CHAR(8) NOT NULL,
    GiamGia FLOAT NOT NULL DEFAULT 0 CHECK (GiamGia >= 0),
    CONSTRAINT PK_CTLKM PRIMARY KEY (MaKM, MaSanPham),
    CONSTRAINT FK_CTLKM_KM FOREIGN KEY (MaKM) REFERENCES KhuyenMai(MaKM),
    CONSTRAINT FK_CTLKM_SP FOREIGN KEY (MaSanPham) REFERENCES SanPham(MaSanPham)
);

-- 15) PhieuTra
CREATE TABLE PhieuTra (
    MaPhieuTra CHAR(8) NOT NULL PRIMARY KEY, -- PTxxxxxx
    NgayLapPhieu DATE NOT NULL CHECK (NgayLapPhieu <= CAST(GETDATE() AS DATE)),
    MaNhanVien CHAR(12) NOT NULL,
    MaKhachHang CHAR(7) NOT NULL,
    TongTienHoan DECIMAL(18,2) NULL CHECK (TongTienHoan >= 0),
    TrangThai NVARCHAR(20) NOT NULL CHECK (TrangThai IN (N'Đã nhập lại hàng', N'Đã huỷ hàng', N'Đang chờ duyệt')),
    CONSTRAINT FK_PT_NV FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien),
    CONSTRAINT FK_PT_KH FOREIGN KEY (MaKhachHang) REFERENCES KhachHang(MaKhachHang),
    CONSTRAINT CK_PT_Ma CHECK (MaPhieuTra LIKE 'PT[0-9][0-9][0-9][0-9][0-9][0-9]' AND LEN(MaPhieuTra)=8)
);

-- 16) ChiTietPhieuTra
CREATE TABLE ChiTietPhieuTra (
    MaPhieuTra CHAR(8) NOT NULL,
    MaHoaDon CHAR(16) NOT NULL,
    MaSanPham CHAR(8) NOT NULL, 
    LyDoChiTiet NVARCHAR(200) NULL,
    SoLuong INT NOT NULL CHECK (SoLuong > 0),
    ThanhTienHoan MONEY NOT NULL CHECK (ThanhTienHoan >= 0),
    CONSTRAINT PK_CTPT PRIMARY KEY (MaPhieuTra, MaHoaDon, MaSanPham),
    CONSTRAINT FK_CTPT_PT FOREIGN KEY (MaPhieuTra) REFERENCES PhieuTra(MaPhieuTra),
    CONSTRAINT FK_CTPT_CTHD FOREIGN KEY (MaHoaDon, MaSanPham) REFERENCES ChiTietHoaDon(MaHoaDon, MaSanPham)
);

-- 17) PhieuHuy
CREATE TABLE PhieuHuy (
    MaPhieuHuy CHAR(16) NOT NULL PRIMARY KEY, -- PH-YYYYMMDD-ssss (16)
    NgayLapPhieu DATE NOT NULL CHECK (NgayLapPhieu <= CAST(GETDATE() AS DATE)),
    MaNhanVien CHAR(12) NOT NULL,
    TrangThai BIT NOT NULL DEFAULT 0 CHECK (TrangThai IN (0,1)),
    CONSTRAINT FK_PH_NV FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien),
    CONSTRAINT CK_PH_Ma CHECK (MaPhieuHuy LIKE 'PH-[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]-[0-9][0-9][0-9][0-9]' AND LEN(MaPhieuHuy)=16)
);

-- 18) ChiTietPhieuHuy
CREATE TABLE ChiTietPhieuHuy (
    MaPhieuHuy CHAR(16) NOT NULL,
    MaLo CHAR(9) NOT NULL,
    SoLuongHuy INT NOT NULL CHECK (SoLuongHuy > 0),
    LyDoChiTiet NVARCHAR(500) NULL,
    DonGiaNhap DECIMAL(18,2) NULL CHECK (DonGiaNhap > 0), 
    ThanhTien AS (CASE WHEN DonGiaNhap IS NULL THEN NULL
                       ELSE CAST(SoLuongHuy AS DECIMAL(18,2)) * DonGiaNhap END) PERSISTED,
    CONSTRAINT PK_CTPH PRIMARY KEY (MaPhieuHuy, MaLo),
    CONSTRAINT FK_CTPH_PH FOREIGN KEY (MaPhieuHuy) REFERENCES PhieuHuy(MaPhieuHuy),
    CONSTRAINT FK_CTPH_Lo FOREIGN KEY (MaLo) REFERENCES LoSanPham(MaLo)
);
GO

/* ===========================================================
   2) DỮ LIỆU MẪU (ĐÃ CHỈNH SỬA VÀ BỔ SUNG)
   =========================================================== */

-- DonViTinh
INSERT INTO DonViTinh (MaDonViTinh, TenDonViTinh, MoTa) VALUES
('DVT-001', N'Viên', N'Đơn vị nhỏ nhất'),
('DVT-002', N'Vỉ',  N'1 vỉ = 10 viên'),
('DVT-003', N'Hộp', N'1 hộp = 10 vỉ');

-- SanPham (GiáBan thỏa bậc lợi nhuận mới)
INSERT INTO SanPham (MaSanPham, TenSanPham, LoaiSanPham, SoDangKy, DuongDung, GiaNhap, GiaBan, HinhAnh, KeBanSanPham, HoatDong) VALUES
('SP000001', N'Paracetamol 500mg', N'THUOC', 'VD-12345-22', N'UONG',  250,  400, 'paracetamol.jpg', 'Kệ A1', 1), 
('SP000002', N'Panadol Extra',      N'THUOC', 'VD-54321-22', N'UONG',  300,  450, 'panadol_extra.jpg','Kệ A2', 1), 
('SP000003', N'Efferalgan 500mg',  N'THUOC', 'VD-67890-23', N'UONG',  280,  420, 'efferalgan.jpg',    'Kệ B1', 1), 
('SP000004', N'Vitamin C 500mg',   N'THUC_PHAM_BO_SUNG','VD-55555-24', N'UONG',  200,  300, 'vitaminc.jpg',    'Kệ C1', 1), 
('SP000005', N'Bông y tế',          N'VAT_TU', 'VD-99999-24', NULL,       50,  100, 'bongyte.jpg',      'Kệ D1', 1); 

-- QuyCachDongGoi
INSERT INTO QuyCachDongGoi (MaSanPham, MaDonViTinh, HeSoQuyDoi, TiLeGiam, DonViGoc) VALUES
('SP000001','DVT-001',1,0,1), ('SP000001','DVT-002',10,0.05,0),('SP000001','DVT-003',100,0.1,0),
('SP000002','DVT-001',1,0,1), ('SP000002','DVT-002',10,0.05,0),
('SP000003','DVT-001',1,0,1), ('SP000003','DVT-002',10,0.05,0),
('SP000004','DVT-001',1,0,1), ('SP000004','DVT-002',10,0.05,0),
('SP000005','DVT-001',1,0,1), ('SP000005','DVT-002',10,0.05,0);

-- LoSanPham
INSERT INTO LoSanPham (MaLo, HanSuDung, SoLuongTon, MaSanPham) VALUES
('LO-000001','2026-03-01',1200,'SP000001'),
('LO-000002','2026-06-01', 800,'SP000002'),
('LO-000003','2026-09-01', 600,'SP000003'),
('LO-000004','2026-12-01', 900,'SP000004'),
('LO-000005','2026-08-01', 400,'SP000005'),
('LO-000006','2026-07-15', 100,'SP000001');

-- TaiKhoan
INSERT INTO TaiKhoan (MaTaiKhoan, TenDangNhap, MatKhau) VALUES
('TK000001','admin','admin@123'),
('TK000002','nvbanhang','12345678'),
('TK000003','nvkho','12345678');

-- NhanVien
INSERT INTO NhanVien (MaNhanVien, TenNhanVien, GioiTinh, NgaySinh, DiaChi, SoDienThoai, QuanLy, MaTaiKhoan, CaLamId, TrangThai) VALUES
('NV2025100001',N'Nguyễn Văn A',1,'1995-05-10',N'Q1, TP.HCM','0909123123',1,'TK000001','SANG',1),
('NV2025100002',N'Trần Thị B', 0,'1998-02-14',N'Q3, TP.HCM','0909345345',0,'TK000002','CHIEU',1),
('NV2025100003',N'Lê Quốc C',  1,'1992-09-22',N'Q5, TP.HCM','0909555666',0,'TK000003','TOI',  1);

-- KhachHang
INSERT INTO KhachHang (MaKhachHang, TenKhachHang, GioiTinh, SoDienThoai, NgaySinh) VALUES
('KH-0001', N'Lê Minh', 1, '0909333444',  '1995-05-12'),
('KH-0002', N'Ngọc Lan', 0, '0909111222',  '1998-08-20'),
('KH-0003', N'Trung Kiên', 1, '0909777555', '1990-03-15');


-- NhaCungCap
INSERT INTO NhaCungCap (MaNhaCungCap, TenNhaCungCap, DiaChi, SoDienThoai) VALUES
('NCC-001',N'Dược Hậu Giang',N'Cần Thơ','0292388888'),
('NCC-002',N'Mekophar',N'HCM','0283939393');

-- PhieuNhap + ChiTiet
INSERT INTO PhieuNhap (MaPhieuNhap, NgayNhap, MaNhaCungCap, MaNhanVien, TongTien) VALUES
('PN0000001','2025-10-10','NCC-001','NV2025100003',NULL),
('PN0000002','2025-10-15','NCC-002','NV2025100003',NULL);

INSERT INTO ChiTietPhieuNhap (MaPhieuNhap, MaLo, SoLuongNhap, DonGiaNhap) VALUES
('PN0000001','LO-000001',1000,250.00),
('PN0000001','LO-000002', 500,300.00),
('PN0000002','LO-000003', 400,280.00),
('PN0000002','LO-000004', 500,200.00),
('PN0000002','LO-000005', 300, 50.00);

UPDATE PN
SET TongTien = T.Tong
FROM PhieuNhap PN
JOIN ( SELECT MaPhieuNhap, SUM(ThanhTien) AS Tong
        FROM ChiTietPhieuNhap
        GROUP BY MaPhieuNhap ) T
 ON PN.MaPhieuNhap = T.MaPhieuNhap;

-- KhuyenMai (ĐÃ BỔ SUNG DATA ĐA DẠNG)
INSERT INTO KhuyenMai (MaKM, TenKM, NgayBatDau, NgayKetThuc, TrangThai, KhuyenMaiHoaDon, HinhThucKM, GiaTri, DieuKienApDungHoaDon, SoLuongToiThieu, SoLuongTangThem) VALUES
-- KM-0001: Giảm phần trăm, không điều kiện HĐ, áp dụng cho SP (sẽ dùng ChiTietKhuyenMaiSanPham)
('KM-20251001-0001',N'Giảm 10% Paracetamol','2025-10-01','2025-12-31',1,0,N'GIAM_GIA_PHAN_TRAM',10,0.00,0,0),
-- KM-0002: Giảm phần trăm, có điều kiện HĐ > 1tr (áp dụng cho HĐ)
('KM-20251001-0002',N'Giảm 5% cho hóa đơn >1tr','2025-10-01','2025-12-31',1,1,N'GIAM_GIA_PHAN_TRAM',5,1000000.00,0,0),
-- KM-0003: Giảm tiền cố định (50k), áp dụng cho SP (sẽ dùng ChiTietKhuyenMaiSanPham)
('KM-20251101-0003',N'Giảm 50K cho Panadol','2025-11-01','2026-01-31',1,0,N'GIAM_GIA_TIEN',50000.00,0.00,0,0),
-- KM-0004: Mua X tặng Y (Tặng thêm), áp dụng cho SP, Mua 5 tặng 1
('KM-20251201-0004',N'Mua 5 tặng 1 (Vit C)','2025-12-01','2026-02-28',1,0,N'TANG_THEM',0.00,0.00,5,1);

INSERT INTO ChiTietKhuyenMaiSanPham (MaKM, MaSanPham, GiamGia) VALUES
('KM-20251001-0001','SP000001',10), -- KM-0001 cho SP000001
('KM-20251001-0001','SP000003',10), -- KM-0001 cho SP000003
('KM-20251101-0003','SP000002',0);  -- KM-0003 cho SP000002 (GiaTri đã lưu trong KhuyenMai)

-- HoaDon + ChiTiet
INSERT INTO HoaDon (MaHoaDon, NgayLap, MaNhanVien, MaKhachHang, TongTien, ThuocTheoDon) VALUES
('HD-20251025-0001','2025-10-25','NV2025100001','KH-0001',NULL,0),
('HD-20251026-0002','2025-10-26','NV2025100002','KH-0002',NULL,0);

INSERT INTO ChiTietHoaDon (MaHoaDon, MaSanPham, MaKM, SoLuong, GiaBan) VALUES
('HD-20251025-0001','SP000001','KM-20251001-0001',10,400.00),
('HD-20251025-0001','SP000002',NULL,               5,450.00),
('HD-20251026-0002','SP000003','KM-20251001-0001',  8,420.00),
('HD-20251026-0002','SP000004',NULL,               12,300.00);

UPDATE HD
SET TongTien = T.Tong
FROM HoaDon HD
JOIN ( SELECT MaHoaDon, SUM(ThanhTien) AS Tong
        FROM ChiTietHoaDon
        GROUP BY MaHoaDon ) T
 ON HD.MaHoaDon = T.MaHoaDon;

-- PhieuTra + ChiTiet
INSERT INTO PhieuTra (MaPhieuTra, NgayLapPhieu, MaNhanVien, MaKhachHang, TongTienHoan, TrangThai) VALUES
('PT000001','2025-10-27','NV2025100001','KH-0001',NULL,N'Đã nhập lại hàng'); 

INSERT INTO ChiTietPhieuTra (MaPhieuTra, MaHoaDon, MaSanPham, LyDoChiTiet, SoLuong, ThanhTienHoan) VALUES
('PT000001','HD-20251025-0001','SP000001',N'Khách đổi ý',2,800.00); 

UPDATE PT
SET TongTienHoan = T.Tong
FROM PhieuTra PT
JOIN ( SELECT MaPhieuTra, SUM(ThanhTienHoan) AS Tong
        FROM ChiTietPhieuTra
        GROUP BY MaPhieuTra ) T
 ON PT.MaPhieuTra = T.MaPhieuTra;

-- PhieuHuy + ChiTiet
INSERT INTO PhieuHuy (MaPhieuHuy, NgayLapPhieu, MaNhanVien, TrangThai) VALUES
('PH-20251027-0001','2025-10-27','NV2025100003',1);

INSERT INTO ChiTietPhieuHuy (MaPhieuHuy, MaLo, SoLuongHuy, LyDoChiTiet, DonGiaNhap) VALUES
('PH-20251027-0001','LO-000006',5,N'Hư hỏng bao bì',250.00);
GO