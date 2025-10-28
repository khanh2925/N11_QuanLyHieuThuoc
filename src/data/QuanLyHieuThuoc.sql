USE master;
GO
DROP DATABASE  QuanLyHieuThuoc;
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
    MaDonViTinh CHAR(7) NOT NULL PRIMARY KEY,
    TenDonViTinh NVARCHAR(50) NOT NULL CHECK (LEN(LTRIM(RTRIM(TenDonViTinh))) > 0),
    MoTa NVARCHAR(200) NULL,
    CONSTRAINT CK_DVT_Ma CHECK (MaDonViTinh LIKE 'DVT-[0-9][0-9][0-9]' AND LEN(MaDonViTinh)=7)
);

-- 2) SanPham
CREATE TABLE SanPham (
    MaSanPham CHAR(8) NOT NULL PRIMARY KEY,
    TenSanPham NVARCHAR(100) NOT NULL CHECK (LEN(LTRIM(RTRIM(TenSanPham))) > 0),
    LoaiSanPham NVARCHAR(50) NOT NULL
        CHECK (LoaiSanPham IN (N'THUOC', N'VAT_TU', N'THUC_PHAM_BO_SUNG', N'THIET_BI_Y_TE')),
    SoDangKy NVARCHAR(20) NULL,
    DuongDung NVARCHAR(10) NULL
        CHECK (DuongDung IN (N'UONG', N'TIEM', N'NHO', N'BOI', N'HIT', N'NGAM', N'DAT', N'DAN')),
    GiaNhap FLOAT NOT NULL CHECK (GiaNhap > 0),
    GiaBan FLOAT NOT NULL CHECK (GiaBan > 0),
    HinhAnh NVARCHAR(255) NULL,
    KeBanSanPham NVARCHAR(100) NULL,
    HoatDong BIT NOT NULL DEFAULT 1,

    CONSTRAINT CK_SP_Ma CHECK (MaSanPham LIKE 'SP[0-9][0-9][0-9][0-9][0-9][0-9]' AND LEN(MaSanPham)=8),
    CONSTRAINT UQ_SP_SoDangKy UNIQUE (SoDangKy),
    CONSTRAINT CK_SP_GiaBan_BacLoiNhuan CHECK (
        (GiaNhap < 10000 AND GiaBan >= GiaNhap * 1.5) OR
        (GiaNhap >= 10000 AND GiaNhap < 50000 AND GiaBan >= GiaNhap * 1.3) OR
        (GiaNhap >= 50000 AND GiaNhap < 200000 AND GiaBan >= GiaNhap * 1.2) OR
        (GiaNhap >= 200000 AND GiaBan >= GiaNhap * 1.1)
    )
);

-- 3) QuyCachDongGoi
CREATE TABLE QuyCachDongGoi (
    MaQuyCach CHAR(8) NOT NULL PRIMARY KEY,
    MaSanPham CHAR(8) NOT NULL,
    MaDonViTinh CHAR(7) NOT NULL,
    HeSoQuyDoi INT NOT NULL CHECK (HeSoQuyDoi > 0),
    TiLeGiam FLOAT NOT NULL DEFAULT 0 CHECK (TiLeGiam >= 0 AND TiLeGiam <= 1),
    DonViGoc BIT NOT NULL DEFAULT 0,

    CONSTRAINT CK_QC_Ma CHECK (MaQuyCach LIKE 'QC[0-9][0-9][0-9][0-9][0-9][0-9]' AND LEN(MaQuyCach) = 8),
    CONSTRAINT FK_QC_SanPham FOREIGN KEY (MaSanPham) REFERENCES SanPham(MaSanPham),
    CONSTRAINT FK_QC_DVT FOREIGN KEY (MaDonViTinh) REFERENCES DonViTinh(MaDonViTinh),
    CONSTRAINT UQ_QC_SP_DVT UNIQUE (MaSanPham, MaDonViTinh),
    CONSTRAINT CK_QC_DonViGoc_HeSo CHECK (
        (DonViGoc = 1 AND HeSoQuyDoi = 1)
        OR
        (DonViGoc = 0 AND HeSoQuyDoi > 1)
    )
);

-- 4) LoSanPham
CREATE TABLE LoSanPham (
    MaLo CHAR(9) NOT NULL PRIMARY KEY,
    HanSuDung DATE NOT NULL,
    SoLuongNhap INT NOT NULL CHECK (SoLuongNhap >= 0),
    SoLuongTon INT NOT NULL CHECK (SoLuongTon >= 0),
    MaSanPham CHAR(8) NOT NULL,
    CONSTRAINT FK_Lo_SP FOREIGN KEY (MaSanPham) REFERENCES SanPham(MaSanPham),
    CONSTRAINT CK_Lo_Ma CHECK (MaLo LIKE 'LO-[0-9][0-9][0-9][0-9][0-9][0-9]' AND LEN(MaLo) = 9),
    CONSTRAINT CK_Lo_Ton_Nhap CHECK (SoLuongTon <= SoLuongNhap)
);

-- 5) TaiKhoan
CREATE TABLE TaiKhoan (
    MaTaiKhoan CHAR(8) NOT NULL PRIMARY KEY,
    TenDangNhap VARCHAR(30) NOT NULL UNIQUE
        CHECK (LEN(TenDangNhap) BETWEEN 5 AND 30 AND TenDangNhap LIKE '%[0-9A-Za-z]%'),
    MatKhau NVARCHAR(100) NOT NULL CHECK (LEN(MatKhau) >= 8),
    CONSTRAINT CK_TK_Ma CHECK (MaTaiKhoan LIKE 'TK[0-9][0-9][0-9][0-9][0-9][0-9]' AND LEN(MaTaiKhoan)=8)
);

-- 6) NhanVien
CREATE TABLE NhanVien (
    MaNhanVien CHAR(12) NOT NULL PRIMARY KEY,
    TenNhanVien NVARCHAR(50) NOT NULL CHECK (LEN(TRIM(TenNhanVien)) > 0),
    GioiTinh BIT NOT NULL DEFAULT 1,
    NgaySinh DATE NOT NULL CHECK (DATEDIFF(YEAR, NgaySinh, GETDATE()) >= 18),
    SoDienThoai CHAR(10) CHECK (LEN(SoDienThoai)=10 AND SoDienThoai LIKE '0[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]'),
    QuanLy BIT NOT NULL DEFAULT 0,
    MaTaiKhoan CHAR(8) NOT NULL,
    CaLam VARCHAR(10) NOT NULL CHECK (CaLam IN ('SANG','CHIEU','TOI')),
    TrangThai BIT NOT NULL DEFAULT 1,
    DiaChi NVARCHAR(100),
    CONSTRAINT FK_NV_TK FOREIGN KEY (MaTaiKhoan) REFERENCES TaiKhoan(MaTaiKhoan),
    CONSTRAINT CK_NV_Ma CHECK (MaNhanVien LIKE 'NV[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]' AND LEN(MaNhanVien)=12)
);

-- 7) KhachHang
CREATE TABLE KhachHang (
    MaKhachHang CHAR(7) NOT NULL PRIMARY KEY,
    TenKhachHang NVARCHAR(100) NOT NULL CHECK (LEN(TRIM(TenKhachHang)) > 0),
    GioiTinh BIT NOT NULL DEFAULT 1,
    SoDienThoai CHAR(10) NULL CHECK (LEN(SoDienThoai)=10 AND SoDienThoai LIKE '0[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]'),
    NgaySinh DATE NULL CHECK (NgaySinh IS NULL OR DATEDIFF(YEAR, NgaySinh, GETDATE()) >= 6),
    CONSTRAINT CK_KH_Ma CHECK (MaKhachHang LIKE 'KH-[0-9][0-9][0-9][0-9]' AND LEN(MaKhachHang)=7)
);

-- 8) NhaCungCap
CREATE TABLE NhaCungCap (
    MaNhaCungCap CHAR(7) NOT NULL PRIMARY KEY,
    TenNhaCungCap NVARCHAR(100) NOT NULL CHECK (LEN(TRIM(TenNhaCungCap)) > 0),
    DiaChi NVARCHAR(200),
    SoDienThoai CHAR(10) NOT NULL CHECK (LEN(SoDienThoai)=10 AND SoDienThoai LIKE '0[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]'),
    CONSTRAINT CK_NCC_Ma CHECK (MaNhaCungCap LIKE 'NCC-[0-9][0-9][0-9]' AND LEN(MaNhaCungCap)=7)
);

-- 9) PhieuNhap
CREATE TABLE PhieuNhap (
    MaPhieuNhap CHAR(9) NOT NULL PRIMARY KEY,
    NgayNhap DATE NOT NULL CHECK (NgayNhap <= GETDATE()),
    MaNhaCungCap CHAR(7) NOT NULL,
    MaNhanVien CHAR(12) NOT NULL,
    TongTien DECIMAL(15,2) DEFAULT 0 CHECK (TongTien >= 0),
    CONSTRAINT FK_PN_NCC FOREIGN KEY (MaNhaCungCap) REFERENCES NhaCungCap(MaNhaCungCap),
    CONSTRAINT FK_PN_NV FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien),
    CONSTRAINT CK_PN_Ma CHECK (MaPhieuNhap LIKE 'PN[0-9][0-9][0-9][0-9][0-9][0-9][0-9]' AND LEN(MaPhieuNhap)=9)
);

-- 10) ChiTietPhieuNhap
CREATE TABLE ChiTietPhieuNhap (
    MaPhieuNhap CHAR(9) NOT NULL,
    MaLo CHAR(9) NOT NULL,
    SoLuongNhap INT NOT NULL CHECK (SoLuongNhap > 0),
    DonGiaNhap DECIMAL(18,2) NOT NULL CHECK (DonGiaNhap > 0),
    ThanhTien AS (SoLuongNhap * DonGiaNhap) PERSISTED,
    CONSTRAINT PK_CTPN PRIMARY KEY (MaPhieuNhap, MaLo),
    CONSTRAINT FK_CTPN_PN FOREIGN KEY (MaPhieuNhap) REFERENCES PhieuNhap(MaPhieuNhap),
    CONSTRAINT FK_CTPN_Lo FOREIGN KEY (MaLo) REFERENCES LoSanPham(MaLo)
);

-- 11) KhuyenMai
CREATE TABLE KhuyenMai (
    MaKM CHAR(16) NOT NULL PRIMARY KEY,
    TenKM NVARCHAR(200) NOT NULL CHECK (LEN(TRIM(TenKM)) > 0),
    NgayBatDau DATE NOT NULL,
    NgayKetThuc DATE NOT NULL,
    TrangThai BIT NOT NULL DEFAULT 1,
    KhuyenMaiHoaDon BIT NOT NULL DEFAULT 0,
    HinhThucKM NVARCHAR(30) NOT NULL CHECK (HinhThucKM IN (N'GIAM_GIA_PHAN_TRAM', N'GIAM_GIA_TIEN', N'TANG_THEM')),
    GiaTri DECIMAL(18,2) NOT NULL CHECK (GiaTri >= 0),
    DieuKienApDungHoaDon DECIMAL(18,2) NOT NULL DEFAULT 0 CHECK (DieuKienApDungHoaDon >= 0),
    SoLuongToiThieu INT NOT NULL DEFAULT 0 CHECK (SoLuongToiThieu >= 0),
    SoLuongTangThem INT NOT NULL DEFAULT 0 CHECK (SoLuongTangThem >= 0),
    CONSTRAINT CK_KM_Date CHECK (NgayBatDau <= NgayKetThuc),
    CONSTRAINT CK_KM_Ma CHECK (MaKM LIKE 'KM-[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]-[0-9][0-9][0-9][0-9]')
);

-- 12) HoaDon
CREATE TABLE HoaDon (
    MaHoaDon CHAR(16) NOT NULL PRIMARY KEY,
    NgayLap DATE NOT NULL CHECK (NgayLap <= GETDATE()),
    MaNhanVien CHAR(12) NOT NULL,
    MaKhachHang CHAR(7) NOT NULL,
    TongTien DECIMAL(18,2) NULL CHECK (TongTien >= 0),
    ThuocTheoDon BIT NOT NULL DEFAULT 0,
    CONSTRAINT FK_HD_NV FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien),
    CONSTRAINT FK_HD_KH FOREIGN KEY (MaKhachHang) REFERENCES KhachHang(MaKhachHang),
    CONSTRAINT CK_HD_Ma CHECK (MaHoaDon LIKE 'HD-[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]-[0-9][0-9][0-9][0-9]')
);

-- 13) ChiTietHoaDon
CREATE TABLE ChiTietHoaDon (
    MaHoaDon CHAR(16) NOT NULL,
    MaSanPham CHAR(8) NOT NULL,
    MaKM CHAR(16) NULL,
    SoLuong INT NOT NULL CHECK (SoLuong > 0),
    GiaBan DECIMAL(18,2) NOT NULL CHECK (GiaBan > 0),
    CONSTRAINT PK_CTHD PRIMARY KEY (MaHoaDon, MaSanPham),
    CONSTRAINT FK_CTHD_HD FOREIGN KEY (MaHoaDon) REFERENCES HoaDon(MaHoaDon),
    CONSTRAINT FK_CTHD_SP FOREIGN KEY (MaSanPham) REFERENCES SanPham(MaSanPham),
    CONSTRAINT FK_CTHD_KM FOREIGN KEY (MaKM) REFERENCES KhuyenMai(MaKM)
);

-- 14) ChiTietKhuyenMaiSanPham
CREATE TABLE ChiTietKhuyenMaiSanPham (
    MaKM CHAR(16) NOT NULL,
    MaSanPham CHAR(8) NOT NULL,
    CONSTRAINT PK_CTLKM PRIMARY KEY (MaKM, MaSanPham),
    CONSTRAINT FK_CTLKM_KM FOREIGN KEY (MaKM) REFERENCES KhuyenMai(MaKM),
    CONSTRAINT FK_CTLKM_SP FOREIGN KEY (MaSanPham) REFERENCES SanPham(MaSanPham)
);

-- 15) PhieuTra
CREATE TABLE PhieuTra (
    MaPhieuTra CHAR(8) NOT NULL PRIMARY KEY,
    NgayLap DATE NOT NULL CHECK (NgayLap <= GETDATE()),
    MaNhanVien CHAR(12) NOT NULL,
    MaKhachHang CHAR(7) NOT NULL,
    TongTienHoan DECIMAL(18,2) NULL CHECK (TongTienHoan >= 0),
    DaDuyet BIT NOT NULL DEFAULT 0,
    CONSTRAINT FK_PT_NV FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien),
    CONSTRAINT FK_PT_KH FOREIGN KEY (MaKhachHang) REFERENCES KhachHang(MaKhachHang),
    CONSTRAINT CK_PT_Ma CHECK (MaPhieuTra LIKE 'PT[0-9][0-9][0-9][0-9][0-9][0-9]')
);

-- 16) ChiTietPhieuTra
CREATE TABLE ChiTietPhieuTra (
    MaPhieuTra CHAR(8) NOT NULL,
    MaHoaDon CHAR(16) NOT NULL,
    MaSanPham CHAR(8) NOT NULL,
    LyDoChiTiet NVARCHAR(200),
    SoLuong INT NOT NULL CHECK (SoLuong > 0),
    ThanhTienHoan DECIMAL(18,2) NOT NULL DEFAULT 0 CHECK (ThanhTienHoan >= 0),
    TrangThai INT NOT NULL DEFAULT 0 CHECK (TrangThai IN (0,1,2)),
    CONSTRAINT PK_CTPT PRIMARY KEY (MaPhieuTra, MaHoaDon, MaSanPham),
    CONSTRAINT FK_CTPT_PT FOREIGN KEY (MaPhieuTra) REFERENCES PhieuTra(MaPhieuTra),
    CONSTRAINT FK_CTPT_CTHD FOREIGN KEY (MaHoaDon, MaSanPham) REFERENCES ChiTietHoaDon(MaHoaDon, MaSanPham)
);

-- 17) PhieuHuy
CREATE TABLE PhieuHuy (
    MaPhieuHuy CHAR(16) NOT NULL PRIMARY KEY,
    NgayLapPhieu DATE NOT NULL CHECK (NgayLapPhieu <= GETDATE()),
    MaNhanVien CHAR(12) NOT NULL,
    TongTienHuy DECIMAL(18,2) NOT NULL DEFAULT 0 CHECK (TongTienHuy >= 0),
    TrangThai BIT NOT NULL DEFAULT 0,
    CONSTRAINT FK_PH_NV FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien),
    CONSTRAINT CK_PH_Ma CHECK (MaPhieuHuy LIKE 'PH-[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]-[0-9][0-9][0-9][0-9]')
);

-- 18) ChiTietPhieuHuy
CREATE TABLE ChiTietPhieuHuy (
    MaPhieuHuy CHAR(16) NOT NULL,
    MaLo CHAR(9) NOT NULL,
    SoLuongHuy INT NOT NULL CHECK (SoLuongHuy > 0),
    LyDoChiTiet NVARCHAR(500) NULL,
    DonGiaNhap DECIMAL(18,2) NOT NULL CHECK (DonGiaNhap > 0),
    ThanhTien AS (SoLuongHuy * DonGiaNhap) PERSISTED,
    CONSTRAINT PK_CTPH PRIMARY KEY (MaPhieuHuy, MaLo),
    CONSTRAINT FK_CTPH_PH FOREIGN KEY (MaPhieuHuy) REFERENCES PhieuHuy(MaPhieuHuy),
    CONSTRAINT FK_CTPH_Lo FOREIGN KEY (MaLo) REFERENCES LoSanPham(MaLo)
);
GO
USE QuanLyHieuThuoc;
GO

/* ===========================================================
   RESET VÀ LÀM SẠCH DỮ LIỆU
   =========================================================== */

-- Tắt kiểm tra ràng buộc khóa ngoại tạm thời để xóa dữ liệu
EXEC sp_MSforeachtable "ALTER TABLE ? NOCHECK CONSTRAINT all"
GO

-- Xóa dữ liệu theo thứ tự phụ thuộc
DELETE FROM ChiTietPhieuTra;
DELETE FROM PhieuTra;
DELETE FROM ChiTietHoaDon;
DELETE FROM HoaDon;
DELETE FROM ChiTietKhuyenMaiSanPham;
DELETE FROM KhuyenMai;
DELETE FROM ChiTietPhieuHuy;
DELETE FROM PhieuHuy;
DELETE FROM ChiTietPhieuNhap;
DELETE FROM PhieuNhap;
DELETE FROM LoSanPham;
DELETE FROM QuyCachDongGoi;
DELETE FROM SanPham;
DELETE FROM DonViTinh;
DELETE FROM NhanVien;
DELETE FROM TaiKhoan;
DELETE FROM KhachHang;
DELETE FROM NhaCungCap;
GO

-- Bật lại kiểm tra ràng buộc khóa ngoại
EXEC sp_MSforeachtable "ALTER TABLE ? WITH CHECK CHECK CONSTRAINT all"
GO

/* ===========================================================
   2) INSERT DỮ LIỆU CƠ BẢN VÀ DANH MỤC (NO NULL)
   =========================================================== */

-- 1) DonViTinh
INSERT INTO DonViTinh (MaDonViTinh, TenDonViTinh, MoTa) VALUES
('DVT-001', N'Viên', N'Đơn vị tính nhỏ nhất cho thuốc viên/con nhộng'),
('DVT-002', N'Vỉ', N'Đơn vị đóng gói lớn hơn viên'),
('DVT-003', N'Hộp', N'Đơn vị đóng gói ngoài cùng'),
('DVT-004', N'Chai', N'Đơn vị tính cho thuốc dạng lỏng/nước'),
('DVT-005', N'Tuýp', N'Đơn vị tính cho thuốc dạng kem/mỡ bôi'),
('DVT-006', N'Gói', N'Đơn vị tính cho thuốc bột/pha dung dịch'),
('DVT-007', N'Cái', N'Đơn vị tính cho vật tư y tế');


-- 5) TaiKhoan
INSERT INTO TaiKhoan (MaTaiKhoan, TenDangNhap, MatKhau) VALUES
('TK000001', 'admin01', N'Admin@12345'), -- Quản lý
('TK000002', 'nhanvien01', N'Nv@1234567'), -- Nhân viên bán hàng
('TK000003', 'nhanvien02', N'Nv@1234567'); -- Nhân viên kho


-- 6) NhanVien (NgaySinh >= 18 tuổi, DiaChi luôn có)
INSERT INTO NhanVien (MaNhanVien, TenNhanVien, GioiTinh, NgaySinh, DiaChi, SoDienThoai, QuanLy, MaTaiKhoan, CaLam, TrangThai) VALUES
('NV2020102002', N'Trần Thị Mai', 0, '2000-01-15', N'45 Phan Văn Trị, Gò Vấp, TP.HCM', '0987111222', 1, 'TK000001', 'SANG', 1),
('NV2020102001', N'Lê Văn Tám', 1, '1995-11-20', N'10 Nguyễn Du, Q.1, TP.HCM', '0987333444', 0, 'TK000002', 'CHIEU', 1),
('NV2020102003', N'Phạm Thu Hà', 0, '2002-03-08', N'99 Lý Thường Kiệt, Q.10, TP.HCM', '0336555666', 0, 'TK000003', 'TOI', 1);


-- 7) KhachHang (SoDienThoai, NgaySinh không NULL/luôn có giá trị hợp lệ)
INSERT INTO KhachHang (MaKhachHang, TenKhachHang, GioiTinh, SoDienThoai, NgaySinh) VALUES
('KH-0001', N'Khách vãng lai', 1, '0900000000', '1990-01-01'), -- Khách mặc định
('KH-0002', N'Nguyễn Hoàng Nam', 1, '0912345678', '1985-06-01'),
('KH-0003', N'Trần Ánh Tuyết', 0, '0398765432', '1998-12-25'),
('KH-0004', N'Lê Kim Anh', 0, '0909090909', '2015-05-10'); -- Đủ 6 tuổi


-- 8) NhaCungCap
INSERT INTO NhaCungCap (MaNhaCungCap, TenNhaCungCap, DiaChi, SoDienThoai) VALUES
('NCC-001', N'Công ty Dược phẩm A.B.C', N'123 Đường Nguyễn Trãi, Q.5, TP.HCM', '0901234567'),
('NCC-002', N'Thiết bị Y tế Hưng Thịnh', N'45 Lê Lợi, Q.1, TP.HCM', '0907654321'),
('NCC-003', N'Công ty Vật tư Y Tế P.Q.R', N'789 Trường Chinh, Hà Nội', '0369876543');


-- 2) SanPham (SoDangKy, DuongDung, HinhAnh, KeBanSanPham đều không NULL)
INSERT INTO SanPham (MaSanPham, TenSanPham, LoaiSanPham, SoDangKy, DuongDung, GiaNhap, GiaBan, HinhAnh, KeBanSanPham, HoatDong) VALUES
-- Bậc 1: GiaNhap < 10000 (Lợi nhuận >= 1.5)
('SP000001', N'Paracetamol 500mg', N'THUOC', 'VD-12345-19', N'UONG', 5000.0, 7500.0, 'paracetamol.png', N'Kệ A-1', 1),
-- Bậc 2: 10000 <= GiaNhap < 50000 (Lợi nhuận >= 1.3)
('SP000003', N'Thuốc ho Bổ Phế', N'THUOC', 'HD-99887-22', N'UONG', 40000.0, 52000.0, 'thuoc_ho.png', N'Kệ A-3', 1),
('SP000004', N'Kem chống hăm Bepanthen', N'THUC_PHAM_BO_SUNG', 'SK-4567-20', N'BOI', 35000.0, 48000.0, 'bepanthen.jpg', N'Kệ B-2', 1),
-- Bậc 3: 50000 <= GiaNhap < 200000 (Lợi nhuận >= 1.2)
('SP000005', N'Máy đo huyết áp Omron', N'THIET_BI_Y_TE', 'TTBYT-001', N'DAN', 150000.0, 180000.0, 'huyet_ap.png', N'Kệ E-1', 1),
('SP000006', N'Vitamin C 1000mg', N'THUC_PHAM_BO_SUNG', 'CN-0011-23', N'UONG', 80000.0, 96000.0, 'vitamin_c.jpg', N'Kệ C-4', 1),
-- Bậc 4: GiaNhap >= 200000 (Lợi nhuận >= 1.1)
('SP000007', N'Glucophage 500mg', N'THUOC', 'VD-77777-21', N'UONG', 250000.0, 275000.0, 'glucophage.png', N'Kệ A-2', 1),
('SP000008', N'Khẩu trang N95 (Hộp 50 cái)', N'VAT_TU', 'VTYT-900', N'NHO', 220000.0, 245000.0, 'khau_trang.jpg', N'Kệ D-1', 1);


-- 3) QuyCachDongGoi (Đảm bảo DonViGoc = 1)
INSERT INTO QuyCachDongGoi (MaQuyCach, MaSanPham, MaDonViTinh, HeSoQuyDoi, TiLeGiam, DonViGoc) VALUES
('QC000001', 'SP000001', 'DVT-001', 1, 0.00, 1), -- SP000001: Viên (Gốc)
('QC000002', 'SP000001', 'DVT-002', 10, 0.05, 0), -- Vỉ
('QC000003', 'SP000003', 'DVT-004', 1, 0.00, 1), -- SP000003: Chai (Gốc)
('QC000004', 'SP000003', 'DVT-003', 2, 0.10, 0), -- Hộp (2 chai)
('QC000005', 'SP000006', 'DVT-001', 1, 0.00, 1), -- SP000006: Viên (Gốc)
('QC000006', 'SP000006', 'DVT-003', 30, 0.15, 0); -- Hộp (30 viên)


-- 4) LoSanPham (HanSuDung > GETDATE())
INSERT INTO LoSanPham (MaLo, HanSuDung, SoLuongNhap, SoLuongTon, MaSanPham) VALUES
('LO-000001', '2026-10-30', 5000, 4900, 'SP000001'),
('LO-000002', '2027-01-20', 300, 280, 'SP000004'),
('LO-000003', '2028-11-11', 50, 50, 'SP000005'),
('LO-000004', '2026-06-01', 1000, 1000, 'SP000006');


-- 9) PhieuNhap
INSERT INTO PhieuNhap (MaPhieuNhap, NgayNhap, MaNhaCungCap, MaNhanVien, TongTien) VALUES
('PN0000001', '2025-10-20', 'NCC-001', 'NV2020102001', 25000000.00), -- Tính toán giả định
('PN0000002', '2025-10-25', 'NCC-002', 'NV2020102001', 7500000.00),
('PN0000003', '2025-10-28', 'NCC-001', 'NV2020102001', 80000000.00);


-- 10) ChiTietPhieuNhap (ThanhTien được tính tự động)
INSERT INTO ChiTietPhieuNhap (MaPhieuNhap, MaLo, SoLuongNhap, DonGiaNhap) VALUES
('PN0000001', 'LO-000001', 5000, 5000.00),
('PN0000002', 'LO-000003', 50, 150000.00),
('PN0000003', 'LO-000004', 1000, 80000.00);


/* ===========================================================
   3) INSERT DỮ LIỆU KHUYẾN MÃI (FULL CASE)
   =========================================================== */

-- KM1: Giảm % HĐ (KhuyenMaiHoaDon = 1)
INSERT INTO KhuyenMai (MaKM, TenKM, NgayBatDau, NgayKetThuc, TrangThai, KhuyenMaiHoaDon, HinhThucKM, GiaTri, DieuKienApDungHoaDon, SoLuongToiThieu, SoLuongTangThem) VALUES
('KM-20251001-0001', N'Giảm 10% HĐ Lớn', '2025-10-01', '2025-11-30', 1, 1, N'GIAM_GIA_PHAN_TRAM', 10.00, 500000.00, 0, 0);

-- KM2: Giảm tiền HĐ (KhuyenMaiHoaDon = 1)
INSERT INTO KhuyenMai (MaKM, TenKM, NgayBatDau, NgayKetThuc, TrangThai, KhuyenMaiHoaDon, HinhThucKM, GiaTri, DieuKienApDungHoaDon, SoLuongToiThieu, SoLuongTangThem) VALUES
('KM-20251101-0002', N'Giảm 50K cho HĐ từ 300K', '2025-11-01', '2025-11-30', 1, 1, N'GIAM_GIA_TIEN', 50000.00, 300000.00, 0, 0);

-- KM3: Giảm % SP (KhuyenMaiHoaDon = 0, GIAM_GIA_PHAN_TRAM) - Không điều kiện SL
INSERT INTO KhuyenMai (MaKM, TenKM, NgayBatDau, NgayKetThuc, TrangThai, KhuyenMaiHoaDon, HinhThucKM, GiaTri, DieuKienApDungHoaDon, SoLuongToiThieu, SoLuongTangThem) VALUES
('KM-20251028-0003', N'Giảm 20% Paracetamol', '2025-10-28', '2025-11-05', 1, 0, N'GIAM_GIA_PHAN_TRAM', 20.00, 0.0, 0, 0);

-- KM4: Tặng thêm SP (KhuyenMaiHoaDon = 0, TANG_THEM) - Có điều kiện SL (Mua 5 tặng 1)
INSERT INTO KhuyenMai (MaKM, TenKM, NgayBatDau, NgayKetThuc, TrangThai, KhuyenMaiHoaDon, HinhThucKM, GiaTri, DieuKienApDungHoaDon, SoLuongToiThieu, SoLuongTangThem) VALUES
('KM-20251115-0004', N'Mua 5 tặng 1 Vitamin C', '2025-11-15', '2025-12-15', 1, 0, N'TANG_THEM', 0.0, 0.0, 5, 1);

-- KM5: Giảm % SP (KhuyenMaiHoaDon = 0, GIAM_GIA_PHAN_TRAM) - Có điều kiện SL (Mua 3 giảm 15%)
INSERT INTO KhuyenMai (MaKM, TenKM, NgayBatDau, NgayKetThuc, TrangThai, KhuyenMaiHoaDon, HinhThucKM, GiaTri, DieuKienApDungHoaDon, SoLuongToiThieu, SoLuongTangThem) VALUES
('KM-20251201-0005', N'Mua 3 hộp Thuốc ho giảm 15%', '2025-12-01', '2025-12-30', 1, 0, N'GIAM_GIA_PHAN_TRAM', 15.00, 0.0, 3, 0);

-- 14) ChiTietKhuyenMaiSanPham
INSERT INTO ChiTietKhuyenMaiSanPham (MaKM, MaSanPham) VALUES
('KM-20251028-0003', 'SP000001'), -- Giảm 20% Paracetamol
('KM-20251115-0004', 'SP000006'), -- Mua 5 tặng 1 Vitamin C
('KM-20251201-0005', 'SP000003'); -- Mua 3 hộp Thuốc ho giảm 15%


/* ===========================================================
   4) INSERT DỮ LIỆU GIAO DỊCH (NO NULL)
   =========================================================== */

-- 12) HoaDon (TongTien luôn được tính toán và điền)
INSERT INTO HoaDon (MaHoaDon, NgayLap, MaNhanVien, MaKhachHang, TongTien, ThuocTheoDon) VALUES
('HD-20251028-0001', '2025-10-28', 'NV2020102001', 'KH-0002', 170000.00, 0), -- Hóa đơn không đủ điều kiện KM
('HD-20251028-0002', '2025-10-28', 'NV2020102001', 'KH-0003', 679500.00, 1), -- Hóa đơn đủ điều kiện KM1
('HD-20251028-0003', '2025-10-28', 'NV2020102001', 'KH-0004', 21450.00, 0); -- Hóa đơn chỉ dùng SP KM3 


-- 13) ChiTietHoaDon (MaKM điền NULL nếu không áp dụng)
INSERT INTO ChiTietHoaDon (MaHoaDon, MaSanPham, MaKM, SoLuong, GiaBan) VALUES
-- HĐ 0001: Mua SP000004 (Không KM) và SP000005 (Không KM). Tổng: 48000 + 150000 = 198,000
('HD-20251028-0001', 'SP000004', NULL, 1, 48000.00), 
('HD-20251028-0001', 'SP000005', NULL, 1, 122000.00), -- 122,000 (Giá bán)

-- HĐ 0002: Tổng trước KM HĐ: (SP000006: 6*96000) + (SP000007: 1*275000) = 576,000 + 275,000 = 851,000.
-- SP000006 áp dụng KM4 (Tặng thêm): Tính tiền 5 viên, bán 6. Tổng: (5*96000) + 275000 = 480,000 + 275,000 = 755,000
-- Áp dụng KM1 (10% HĐ > 500k): 755,000 * 90% = 679,500.00 -> Tổng HĐ 0002 là 679,500.00 (Đã điền ở trên)
('HD-20251028-0002', 'SP000006', 'KM-20251115-0004', 6, 96000.00), 
('HD-20251028-0002', 'SP000007', NULL, 1, 275000.00),

-- HĐ 0003: Mua SP000001 (KM3: giảm 20%). Tổng: 3*7500 * 80% = 18,000
('HD-20251028-0003', 'SP000001', 'KM-20251028-0003', 3, 7500.00); -- 3 viên Paracetamol

-- 15) PhieuTra (TongTienHoan luôn tính toán và điền)
INSERT INTO PhieuTra (MaPhieuTra, NgayLap, MaNhanVien, MaKhachHang, TongTienHoan, DaDuyet) VALUES
('PT000001', '2025-10-28', 'NV2020102001', 'KH-0003', 96000.00, 1); -- Đã duyệt

-- 16) ChiTietPhieuTra (LyDoChiTiet luôn điền, TrangThai 0/1/2)
INSERT INTO ChiTietPhieuTra (MaPhieuTra, MaHoaDon, MaSanPham, LyDoChiTiet, SoLuong, ThanhTienHoan, TrangThai) VALUES
('PT000001', 'HD-20251028-0002', 'SP000006', N'Khách trả lại do mua dư, đã duyệt', 1, 96000.00, 1); -- Nhập lại (TrangThai=1)

-- 17) PhieuHuy
INSERT INTO PhieuHuy (MaPhieuHuy, NgayLapPhieu, MaNhanVien, TongTienHuy, TrangThai) VALUES
('PH-20251028-0001', '2025-10-28', 'NV2020102001', 500000.00, 1); -- Đã duyệt

-- 18) ChiTietPhieuHuy (LyDoChiTiet luôn điền)
INSERT INTO ChiTietPhieuHuy (MaPhieuHuy, MaLo, SoLuongHuy, LyDoChiTiet, DonGiaNhap) VALUES
('PH-20251028-0001', 'LO-000001', 100, N'Hủy do sản phẩm gần hết hạn sử dụng', 5000.00);
GO

Select * from NhanVien 
