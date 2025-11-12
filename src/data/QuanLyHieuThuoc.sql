------------------------------------------------------------
-- 🧹 1. XÓA VÀ TẠO LẠI DATABASE
------------------------------------------------------------
USE master;
IF EXISTS (SELECT name FROM sys.databases WHERE name = N'QuanLyHieuThuoc')
BEGIN
    ALTER DATABASE QuanLyHieuThuoc SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE QuanLyHieuThuoc;
END;
GO

CREATE DATABASE QuanLyHieuThuoc;
GO
USE QuanLyHieuThuoc;
GO

------------------------------------------------------------
-- 2. DANH MỤC CƠ BẢN
------------------------------------------------------------

CREATE TABLE DonViTinh (
    MaDonViTinh CHAR(7) PRIMARY KEY CHECK (MaDonViTinh LIKE 'DVT-%'),
    TenDonViTinh NVARCHAR(50) NOT NULL
);
GO

CREATE TABLE NhanVien (
    MaNhanVien CHAR(17) PRIMARY KEY CHECK (MaNhanVien LIKE 'NV-%'),
    TenNhanVien NVARCHAR(50) NOT NULL,
    GioiTinh BIT NOT NULL,
    NgaySinh DATE NOT NULL CHECK (DATEDIFF(YEAR, NgaySinh, GETDATE()) >= 18),
    SoDienThoai CHAR(10) NOT NULL CHECK (SoDienThoai LIKE '0[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]'),
    DiaChi NVARCHAR(100) NOT NULL,
    QuanLy BIT NOT NULL DEFAULT 0,
    CaLam TINYINT NOT NULL CHECK (CaLam BETWEEN 1 AND 3),
    TrangThai BIT NOT NULL DEFAULT 1
);
GO

CREATE TABLE TaiKhoan (
    MaTaiKhoan CHAR(17) PRIMARY KEY CHECK (MaTaiKhoan LIKE 'TK-%'),
    TenDangNhap VARCHAR(30) NOT NULL UNIQUE,
    MatKhau VARCHAR(100) NOT NULL,
    MaNhanVien CHAR(17) NOT NULL,
    CONSTRAINT FK_TaiKhoan_NhanVien FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien)
);
GO

CREATE TABLE KhachHang (
    MaKhachHang CHAR(17) PRIMARY KEY CHECK (MaKhachHang LIKE 'KH-%'),
    TenKhachHang NVARCHAR(100) NOT NULL,
    GioiTinh BIT NOT NULL,
    SoDienThoai CHAR(10) NOT NULL CHECK (soDienThoai LIKE '0[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]'),
    NgaySinh DATE NOT NULL,
    HoatDong BIT NOT NULL DEFAULT 1,
    DiemTichLuy FLOAT DEFAULT 0
);
GO

CREATE TABLE NhaCungCap (
    MaNhaCungCap CHAR(17) PRIMARY KEY CHECK (maNhaCungCap LIKE 'NCC-%'),
    TenNhaCungCap NVARCHAR(100) NOT NULL,
    SoDienThoai CHAR(10) NOT NULL,
    DiaChi NVARCHAR(200) NOT NULL,
    Email NVARCHAR(100),
    HoatDong BIT NOT NULL DEFAULT 1
);
GO

CREATE TABLE BangGia (
    MaBangGia CHAR(17) PRIMARY KEY CHECK (MaBangGia LIKE 'BG-%'),
    MaNhanVien CHAR(17) NOT NULL,
    TenBangGia NVARCHAR(100) NOT NULL,
    NgayApDung DATE NOT NULL,
    HoatDong BIT NOT NULL DEFAULT 1,
    CONSTRAINT FK_BangGia_NhanVien FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien)
);
GO

CREATE TABLE KhuyenMai (
    MaKM CHAR(17) PRIMARY KEY CHECK (MaKM LIKE 'KM-%'),
    TenKM NVARCHAR(200) NOT NULL,
    NgayBatDau DATE NOT NULL,
    NgayKetThuc DATE NOT NULL,
    TrangThai BIT NOT NULL,
    KhuyenMaiHoaDon BIT NOT NULL,
    HinhThuc VARCHAR(30) NOT NULL CHECK (HinhThuc IN ('GIAM_GIA_PHAN_TRAM','GIAM_GIA_TIEN','TANG_THEM')),
    GiaTri FLOAT NOT NULL,
    DieuKienApDungHoaDon FLOAT DEFAULT 0,
    SoLuongKhuyenMai INT DEFAULT 0
);
GO

------------------------------------------------------------
-- 3. SẢN PHẨM & QUY CÁCH
------------------------------------------------------------

CREATE TABLE SanPham (
    MaSanPham CHAR(9) PRIMARY KEY CHECK (MaSanPham LIKE 'SP-%'),
    TenSanPham NVARCHAR(100) NOT NULL,
    LoaiSanPham VARCHAR(50) NOT NULL CHECK (LoaiSanPham IN 
        ('THUOC','THUC_PHAM_BO_SUNG','MY_PHAM','DUNG_CU_Y_TE','SAN_PHAM_CHO_ME_VA_BE','SAN_PHAM_KHAC')),
    SoDangKy VARCHAR(20),
    DuongDung VARCHAR(20) CHECK (DuongDung IN ('UONG','TIEM','NHO','BOI','HIT','NGAM','DAT','DAN')),
    GiaNhap FLOAT NOT NULL CHECK (GiaNhap > 0),
    GiaBan FLOAT,
    HinhAnh NVARCHAR(255),
    KeBanSanPham NVARCHAR(100),
    HoatDong BIT NOT NULL DEFAULT 1
);
GO

CREATE TABLE LoSanPham (
    MaLo CHAR(9) PRIMARY KEY CHECK (maLo LIKE 'LO-%'),
    HanSuDung DATE NOT NULL,
    SoLuongTon INT DEFAULT 0,
    MaSanPham CHAR(9) NOT NULL,
    CONSTRAINT FK_LoSanPham_SanPham FOREIGN KEY (MaSanPham) REFERENCES SanPham(MaSanPham)
);
GO

CREATE TABLE QuyCachDongGoi (
    MaQuyCach CHAR(9) PRIMARY KEY CHECK (MaQuyCach LIKE 'QC-%'),
    MaDonViTinh CHAR(7) NOT NULL,
    MaSanPham CHAR(9) NOT NULL,
    HeSoQuyDoi INT NOT NULL CHECK (HeSoQuyDoi > 0),
    TiLeGiam FLOAT CHECK (TiLeGiam BETWEEN 0 AND 1),
    DonViGoc BIT NOT NULL DEFAULT 0,
    CONSTRAINT FK_QC_DVT FOREIGN KEY (MaDonViTinh) REFERENCES DonViTinh(MaDonViTinh),
    CONSTRAINT FK_QC_SP FOREIGN KEY (MaSanPham) REFERENCES SanPham(MaSanPham)
);
GO

CREATE TABLE ChiTietBangGia (
    MaBangGia CHAR(17) NOT NULL,
    MaSanPham CHAR(9) NOT NULL,
    GiaTu FLOAT NOT NULL,
    GiaDen FLOAT NOT NULL,
    TiLe FLOAT NOT NULL CHECK (tiLe > 0 AND tiLe <= 5),
    CONSTRAINT PK_ChiTietBangGia PRIMARY KEY (MaBangGia, MaSanPham),
    CONSTRAINT FK_CTBangGia_BG FOREIGN KEY (MaBangGia) REFERENCES BangGia(MaBangGia),
    CONSTRAINT FK_CTBangGia_SP FOREIGN KEY (MaSanPham) REFERENCES SanPham(MaSanPham)
);
GO

------------------------------------------------------------
-- 4. NGHIỆP VỤ NHẬP HÀNG & HỦY HÀNG
------------------------------------------------------------

CREATE TABLE PhieuNhap (
    MaPhieuNhap CHAR(17) PRIMARY KEY CHECK (MaPhieuNhap LIKE 'PN-%'),
    NgayNhap DATE NOT NULL,
    MaNhaCungCap CHAR(17) NOT NULL,
    MaNhanVien CHAR(17) NOT NULL,
    TongTien FLOAT DEFAULT 0,
    CONSTRAINT FK_PN_NCC FOREIGN KEY (MaNhaCungCap) REFERENCES NhaCungCap(MaNhaCungCap),
    CONSTRAINT FK_PN_NV FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien)
);
GO

CREATE TABLE ChiTietPhieuNhap (
    MaPhieuNhap CHAR(17) NOT NULL,
    MaLo CHAR(9) NOT NULL,
    MaDonViTinh CHAR(7) NOT NULL,
    SoLuongNhap INT NOT NULL CHECK (SoLuongNhap > 0),
    DonGiaNhap FLOAT NOT NULL CHECK (DonGiaNhap > 0),
    ThanhTien FLOAT,
    CONSTRAINT PK_CTPN PRIMARY KEY (MaPhieuNhap, MaLo),
    CONSTRAINT FK_CTPN_PN FOREIGN KEY (MaPhieuNhap) REFERENCES PhieuNhap(MaPhieuNhap),
    CONSTRAINT FK_CTPN_Lo FOREIGN KEY (MaLo) REFERENCES LoSanPham(MaLo),
    CONSTRAINT FK_CTPN_DVT FOREIGN KEY (MaDonViTinh) REFERENCES DonViTinh(MaDonViTinh)
);
GO

CREATE TABLE PhieuHuy (
    MaPhieuHuy CHAR(17) PRIMARY KEY CHECK (MaPhieuHuy LIKE 'PH-%'),
    NgayLapPhieu DATE NOT NULL,
    MaNhanVien CHAR(17) NOT NULL,
    TrangThai BIT NOT NULL,
    TongTien FLOAT DEFAULT 0,
    CONSTRAINT FK_PH_NV FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien)
);
GO

CREATE TABLE ChiTietPhieuHuy (
    MaPhieuHuy CHAR(17) NOT NULL,
    MaLo CHAR(9) NOT NULL,
    SoLuongHuy INT NOT NULL,
    LyDoChiTiet NVARCHAR(500),
    DonGiaNhap FLOAT NOT NULL,
    ThanhTien FLOAT,
    TrangThai INT CHECK (TrangThai BETWEEN 1 AND 3),
    CONSTRAINT PK_CTPH PRIMARY KEY (MaPhieuHuy, MaLo),
    CONSTRAINT FK_CTPH_PH FOREIGN KEY (MaPhieuHuy) REFERENCES PhieuHuy(MaPhieuHuy),
    CONSTRAINT FK_CTPH_Lo FOREIGN KEY (MaLo) REFERENCES LoSanPham(MaLo)
);
GO

------------------------------------------------------------
-- 5. NGHIỆP VỤ BÁN HÀNG & TRẢ HÀNG
------------------------------------------------------------

CREATE TABLE HoaDon (
    MaHoaDon CHAR(17) PRIMARY KEY CHECK (MaHoaDon LIKE 'HD-%'),
    MaNhanVien CHAR(17) NOT NULL,
    MaKhachHang CHAR(17) NOT NULL,
    NgayLap DATE NOT NULL,
    TongTien FLOAT DEFAULT 0,
    TongThanhToan FLOAT DEFAULT 0,
    DiemSuDung FLOAT DEFAULT 0,
    MaKM CHAR(17) NULL,
    SoTienGiamKhuyenMai FLOAT DEFAULT 0,
    ThuocKeDon BIT NOT NULL DEFAULT 0,
    CONSTRAINT FK_HD_NV FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien),
    CONSTRAINT FK_HD_KH FOREIGN KEY (MaKhachHang) REFERENCES KhachHang(MaKhachHang),
    CONSTRAINT FK_HD_KM FOREIGN KEY (MaKM) REFERENCES KhuyenMai(MaKM)
);
GO

CREATE TABLE ChiTietHoaDon (
    MaHoaDon CHAR(17) NOT NULL,
    MaLo CHAR(9) NOT NULL,
    SoLuong FLOAT NOT NULL,
    GiaBan FLOAT NOT NULL,
    MaKM CHAR(17) NULL,
    ThanhTien FLOAT,
    CONSTRAINT PK_CTHD PRIMARY KEY (MaHoaDon, MaLo),
    CONSTRAINT FK_CTHD_HD FOREIGN KEY (MaHoaDon) REFERENCES HoaDon(MaHoaDon),
    CONSTRAINT FK_CTHD_Lo FOREIGN KEY (MaLo) REFERENCES LoSanPham(MaLo),
    CONSTRAINT FK_CTHD_KM FOREIGN KEY (MaKM) REFERENCES KhuyenMai(MaKM)
);
GO

CREATE TABLE PhieuTra (
    MaPhieuTra CHAR(17) PRIMARY KEY CHECK (MaPhieuTra LIKE 'PT%'),
    MaKhachHang CHAR(17) NOT NULL,
    MaNhanVien CHAR(17) NOT NULL,
    NgayLap DATE NOT NULL,
    DaDuyet BIT DEFAULT 0,
    TongTienHoan FLOAT DEFAULT 0,
    CONSTRAINT FK_PT_KH FOREIGN KEY (MaKhachHang) REFERENCES KhachHang(MaKhachHang),
    CONSTRAINT FK_PT_NV FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien)
);
GO

CREATE TABLE ChiTietPhieuTra (
    MaPhieuTra CHAR(17) NOT NULL,
    MaHoaDon CHAR(17) NOT NULL,
    MaLo CHAR(9) NOT NULL,
    SoLuong INT NOT NULL,
    LyDoChiTiet NVARCHAR(200),
    ThanhTienHoan FLOAT,
    TrangThai INT CHECK (trangThai BETWEEN 0 AND 2),
    CONSTRAINT PK_CTPTRA PRIMARY KEY (MaPhieuTra, MaHoaDon, MaLo),
    CONSTRAINT FK_CTPTRA_PT FOREIGN KEY (MaPhieuTra) REFERENCES PhieuTra(MaPhieuTra),
    CONSTRAINT FK_CTPTRA_HD FOREIGN KEY (MaHoaDon) REFERENCES HoaDon(MaHoaDon),
    CONSTRAINT FK_CTPTRA_Lo FOREIGN KEY (MaLo) REFERENCES LoSanPham(MaLo)
);
GO

------------------------------------------------------------
-- 6. KHÁC: CHI TIẾT KHUYẾN MÃI - SẢN PHẨM
------------------------------------------------------------
CREATE TABLE ChiTietKhuyenMaiSanPham (
    MaKM CHAR(17) NOT NULL,
    MaSanPham CHAR(9) NOT NULL,
    SoLuongToiThieu INT DEFAULT 0,
    SoLuongTangThem INT DEFAULT 0,
    CONSTRAINT PK_CTKMSP PRIMARY KEY (MaKM, MaSanPham),
    CONSTRAINT FK_CTKMSP_KM FOREIGN KEY (MaKM) REFERENCES KhuyenMai(MaKM),
    CONSTRAINT FK_CTKMSP_SP FOREIGN KEY (MaSanPham) REFERENCES SanPham(MaSanPham)
);
GO


-- ************************************************************
-- DỮ LIỆU ĐƯỢC MỞ RỘNG CHO KIỂM THỬ (JTEST) - ĐÃ SỬA KHỚP
-- ************************************************************
DELETE FROM ChiTietPhieuTra;
DELETE FROM PhieuTra;
DELETE FROM ChiTietHoaDon;
DELETE FROM HoaDon;
DELETE FROM ChiTietPhieuHuy;
DELETE FROM PhieuHuy;
DELETE FROM ChiTietPhieuNhap;
DELETE FROM PhieuNhap;
DELETE FROM ChiTietBangGia;
DELETE FROM BangGia;
DELETE FROM ChiTietKhuyenMaiSanPham;
DELETE FROM KhuyenMai;
DELETE FROM LoSanPham;
DELETE FROM QuyCachDongGoi;
DELETE FROM SanPham;
DELETE FROM NhaCungCap;
DELETE FROM KhachHang;
DELETE FROM TaiKhoan;
DELETE FROM NhanVien;
DELETE FROM DonViTinh;
GO

-- ===== 1. DANH MỤC CƠ BẢN =====
INSERT INTO DonViTinh (MaDonViTinh, TenDonViTinh)
VALUES
('DVT-001', N'Viên'),
('DVT-002', N'Vỉ'),
('DVT-003', N'Hộp'),
('DVT-004', N'Chai');
GO

INSERT INTO NhanVien (MaNhanVien, TenNhanVien, GioiTinh, NgaySinh, SoDienThoai, DiaChi, QuanLy, CaLam, TrangThai)
VALUES
('NV-20251105-0001', N'Hoàng Quốc Nhung', 1, '1990-01-01', '0932981590', N'169 Hùng Vương, Q.10, TP.HCM', 1, 1, 1),
('NV-20251105-0002', N'Trần Diễm Oanh', 0, '1995-02-27', '0959630223', N'84 Võ Văn Tần, Q.Bình Thạnh, TP.HCM', 0, 2, 1),
('NV-20251105-0003', N'Nguyễn Văn A', 1, '1985-05-20', '0900111222', N'123 Lê Lợi, Q.1, TP.HCM', 0, 3, 0);
GO

INSERT INTO TaiKhoan (MaTaiKhoan, TenDangNhap, MatKhau, MaNhanVien)
VALUES
('TK-20251105-0001', 'QL_Nhung', 'Matkhau123', 'NV-20251105-0001'),
('TK-20251105-0002', 'NV_Oanh', 'Matkhau123', 'NV-20251105-0002'),
('TK-20251105-0003', 'NV_VanA', 'Matkhau123', 'NV-20251105-0003');
GO

INSERT INTO KhachHang (MaKhachHang, TenKhachHang, GioiTinh, SoDienThoai, NgaySinh, HoatDong, DiemTichLuy)
VALUES
('KH-20000000-0001', N'Khách Vãng Lai', 0, '0000000000', '1983-11-15', 1, 0.0),
('KH-20251105-0001', N'Lê Văn B', 1, '0912345678', '1990-01-01', 1, 1000.0),
('KH-20251105-0002', N'Phạm Thị C', 0, '0987654321', '1998-05-10', 1, 0.0),
('KH-20251105-0003', N'Đỗ Minh D', 1, '0399887766', '1975-12-20', 1, 50.0),
('KH-20251105-0004', N'Vũ Kim E', 0, '0701234567', '2000-02-02', 1, 200.0);
GO

INSERT INTO NhaCungCap (MaNhaCungCap, TenNhaCungCap, SoDienThoai, DiaChi, Email, HoatDong)
VALUES
('NCC-20251105-0001', N'Dược phẩm Trung ương', '0393893008', N'15 Hùng Vương', 'trunguong@pharma.vn', 1),
('NCC-20230101-0001', N'Thiết bị Y tế Z', '0393893008', N'88 Láng Hạ', 'yte_z@corp.vn', 0);
GO

INSERT INTO BangGia (MaBangGia, MaNhanVien, TenBangGia, NgayApDung, HoatDong)
VALUES
('BG-20251105-0001', 'NV-20251105-0001', N'Giá bán lẻ hiện hành', '2025-11-05', 1);
GO

-- ===== 2. SẢN PHẨM & LÔ & QUY CÁCH =====
INSERT INTO SanPham (MaSanPham, TenSanPham, LoaiSanPham, SoDangKy, DuongDung, GiaNhap, GiaBan, KeBanSanPham, HoatDong)
VALUES
('SP-000001', N'Paracetamol 500mg', 'THUOC', 'VN-P500', 'UONG', 10000, 12000, N'Kệ A1', 1),
('SP-000002', N'Vitamin C 1000mg', 'THUC_PHAM_BO_SUNG', 'VN-VC', NULL, 50000, 62500, N'Kệ B1', 1),
('SP-000003', N'Kem chống nắng', 'MY_PHAM', NULL, 'BOI', 120000, 156000, N'Tủ lạnh 1', 1),
('SP-000004', N'Khẩu trang y tế', 'DUNG_CU_Y_TE', 'VN-KT', NULL, 2000, 2400, N'Quầy thuốc 1', 1),
('SP-000005', N'Amoxicillin 500mg', 'THUOC', 'VN-AMX', 'UONG', 8000, 10400, N'Kệ A2', 1),
('SP-000006', N'Siro ho trẻ em', 'THUOC', NULL, 'UONG', 75000, 97500, N'Kệ B2', 1),
('SP-000007', N'Dầu gió', 'SAN_PHAM_KHAC', NULL, NULL, 30000, 39000, N'Quầy 2', 1);
GO

INSERT INTO LoSanPham (MaLo, HanSuDung, SoLuongTon, MaSanPham)
VALUES
('LO-000001', '2028-12-31', 5000, 'SP-000001'),
('LO-000002', '2026-03-01', 100, 'SP-000002'),
('LO-000003', '2025-12-15', 200, 'SP-000004'),
('LO-000004', '2028-10-01', 8000, 'SP-000005'),
('LO-000005', '2027-05-01', 0, 'SP-000003'),
('LO-000006', '2027-08-01', 150, 'SP-000006'),
('LO-000007', '2026-11-01', 300, 'SP-000007');
GO

INSERT INTO QuyCachDongGoi (MaQuyCach, MaDonViTinh, MaSanPham, HeSoQuyDoi, TiLeGiam, DonViGoc)
VALUES
('QC-000001', 'DVT-001', 'SP-000001', 1, 0.0, 1),
('QC-000002', 'DVT-002', 'SP-000001', 10, 0.0, 0),
('QC-000003', 'DVT-003', 'SP-000001', 100, 0.05, 0),
('QC-000004', 'DVT-004', 'SP-000002', 1, 0.0, 1),
('QC-000005', 'DVT-003', 'SP-000004', 50, 0.0, 0),
('QC-000006', 'DVT-001', 'SP-000005', 1, 0.0, 1),
('QC-000007', 'DVT-003', 'SP-000005', 20, 0.0, 0),
('QC-000008', 'DVT-004', 'SP-000006', 1, 0.0, 1),
('QC-000009', 'DVT-003', 'SP-000007', 12, 0.0, 0);
GO

INSERT INTO ChiTietBangGia (MaBangGia, MaSanPham, GiaTu, GiaDen, TiLe)
VALUES
('BG-20251105-0001', 'SP-000001', 12000.0, 12000.0, 1.2),
('BG-20251105-0001', 'SP-000002', 62500.0, 62500.0, 1.25),
('BG-20251105-0001', 'SP-000003', 156000.0, 156000.0, 1.3),
('BG-20251105-0001', 'SP-000004', 2400.0, 2400.0, 1.2),
('BG-20251105-0001', 'SP-000005', 10400.0, 10400.0, 1.3),
('BG-20251105-0001', 'SP-000006', 97500.0, 97500.0, 1.3),
('BG-20251105-0001', 'SP-000007', 39000.0, 39000.0, 1.3);
GO

-- ===== 3. KHUYẾN MÃI =====
INSERT INTO KhuyenMai (MaKM, TenKM, NgayBatDau, NgayKetThuc, TrangThai, KhuyenMaiHoaDon, HinhThuc, GiaTri, DieuKienApDungHoaDon, SoLuongKhuyenMai)
VALUES
-- KM HÓA ĐƠN
('KM-20251101-0001', N'Giảm 50K cho HD > 200K', '2025-11-01', '2025-12-31', 1, 1, 'GIAM_GIA_TIEN', 50000.0, 200000.0, 100),
('KM-20251101-0002', N'Giảm 10% cho HD > 500K', '2025-11-01', '2025-12-31', 1, 1, 'GIAM_GIA_PHAN_TRAM', 10.0, 500000.0, 100),
('KM-20240101-0001', N'Giảm 20% (Đã hết hạn)', '2024-01-01', '2024-01-31', 0, 1, 'GIAM_GIA_PHAN_TRAM', 20.0, 100000.0, 100),
('KM-20251101-0003', N'Giảm 50K (Hết hàng KM)', '2025-11-01', '2025-12-31', 1, 1, 'GIAM_GIA_TIEN', 50000.0, 100000.0, 0),
-- KM SẢN PHẨM
('KM-20251101-0004', N'Giảm 10% Paracetamol', '2025-11-01', '2025-12-31', 1, 0, 'GIAM_GIA_PHAN_TRAM', 10.0, 0.0, 100),
('KM-20251101-0005', N'Giảm 500đ/Viên Amoxicillin', '2025-11-01', '2025-12-31', 1, 0, 'GIAM_GIA_TIEN', 500.0, 0.0, 100),
('KM-20251101-0006', N'Mua 3 Tặng 1 Khẩu trang', '2025-11-01', '2025-12-31', 1, 0, 'TANG_THEM', 0.0, 0.0, 100),
('KM-20251101-0007', N'Giảm 5K Siro Ho', '2025-11-01', '2025-11-05', 1, 0, 'GIAM_GIA_TIEN', 5000.0, 0.0, 50),
('KM-20240101-0002', N'Giảm 15% (Đã hết hạn)', '2024-01-01', '2024-01-31', 0, 0, 'GIAM_GIA_PHAN_TRAM', 15.0, 0.0, 100);
GO

INSERT INTO ChiTietKhuyenMaiSanPham (MaKM, MaSanPham, SoLuongToiThieu, SoLuongTangThem)
VALUES
('KM-20251101-0004', 'SP-000001', 0, 0),
('KM-20251101-0005', 'SP-000005', 0, 0),
('KM-20251101-0006', 'SP-000004', 3, 1),
('KM-20251101-0007', 'SP-000006', 0, 0),
('KM-20240101-0002', 'SP-000002', 0, 0);
GO

-- ===== 4. NHẬP HÀNG (10 PHIẾU) =====
INSERT INTO PhieuNhap (MaPhieuNhap, NgayNhap, MaNhaCungCap, MaNhanVien, TongTien)
VALUES
('PN-20251030-0001', '2025-10-30', 'NCC-20251105-0001', 'NV-20251105-0002', 2000000.0),
('PN-20251101-0001', '2025-11-01', 'NCC-20251105-0001', 'NV-20251105-0002', 1500000.0),
('PN-20251103-0001', '2025-11-03', 'NCC-20251105-0001', 'NV-20251105-0001', 500000.0),
('PN-20251104-0001', '2025-11-04', 'NCC-20230101-0001', 'NV-20251105-0002', 200000.0),
('PN-20251105-0001', '2025-11-05', 'NCC-20251105-0001', 'NV-20251105-0001', 120000.0),
('PN-20250915-0001', '2025-09-15', 'NCC-20251105-0001', 'NV-20251105-0002', 500000.0),
('PN-20241201-0001', '2024-12-01', 'NCC-20251105-0001', 'NV-20251105-0001', 300000.0),
('PN-20240101-0001', '2024-01-01', 'NCC-20251105-0001', 'NV-20251105-0001', 100000.0),
('PN-20230601-0001', '2023-06-01', 'NCC-20251105-0001', 'NV-20251105-0002', 250000.0),
('PN-20230101-0001', '2023-01-01', 'NCC-20251105-0001', 'NV-20251105-0002', 150000.0);
GO

INSERT INTO ChiTietPhieuNhap (MaPhieuNhap, MaLo, MaDonViTinh, SoLuongNhap, DonGiaNhap, ThanhTien)
VALUES
('PN-20251030-0001', 'LO-000001', 'DVT-003', 15, 100000.0, 1500000.0),
('PN-20251030-0001', 'LO-000002', 'DVT-004', 8, 50000.0, 400000.0),
('PN-20251030-0001', 'LO-000006', 'DVT-004', 10, 10000.0, 100000.0),
('PN-20251101-0001', 'LO-000004', 'DVT-003', 100, 10000.0, 1000000.0),
('PN-20251101-0001', 'LO-000007', 'DVT-003', 10, 50000.0, 500000.0),
('PN-20251103-0001', 'LO-000001', 'DVT-002', 200, 2000.0, 400000.0),
('PN-20251103-0001', 'LO-000003', 'DVT-003', 2, 50000.0, 100000.0),
('PN-20251104-0001', 'LO-000003', 'DVT-003', 4, 50000.0, 200000.0),
('PN-20251105-0001', 'LO-000007', 'DVT-004', 4, 30000.0, 120000.0),
('PN-20250915-0001', 'LO-000002', 'DVT-004', 10, 50000.0, 500000.0),
('PN-20241201-0001', 'LO-000004', 'DVT-003', 30, 10000.0, 300000.0),
('PN-20240101-0001', 'LO-000006', 'DVT-004', 1, 100000.0, 100000.0),
('PN-20230601-0001', 'LO-000001', 'DVT-003', 2, 125000.0, 250000.0),
('PN-20230101-0001', 'LO-000002', 'DVT-004', 3, 50000.0, 150000.0);
GO

-- ===== 5. HỦY HÀNG (5 PHIẾU) =====
INSERT INTO PhieuHuy (MaPhieuHuy, NgayLapPhieu, MaNhanVien, TrangThai, TongTien)
VALUES
('PH-20251105-0001', '2025-11-05', 'NV-20251105-0002', 0, 100000.0),
('PH-20251104-0001', '2025-11-04', 'NV-20251105-0001', 1, 50000.0),
('PH-20251001-0001', '2025-10-01', 'NV-20251105-0002', 1, 0.0),
('PH-20240101-0001', '2024-01-01', 'NV-20251105-0001', 1, 20000.0),
('PH-20230501-0001', '2023-05-01', 'NV-20251105-0002', 0, 15000.0);
GO

INSERT INTO ChiTietPhieuHuy (MaPhieuHuy, MaLo, SoLuongHuy, LyDoChiTiet, DonGiaNhap, ThanhTien, TrangThai)
VALUES
('PH-20230501-0001', 'LO-000003', 1, N'Lỗi tem', 15000.0, 15000.0, 1);
('PH-20230501-0001', 'LO-000002', 1, N'Lỗi tem', 15000.0, 15000.0, 1);
('PH-20230501-0001', 'LO-000005', 1, N'Lỗi tem', 15000.0, 15000.0, 1);
('PH-20230501-0001', 'LO-000001', 1, N'Lỗi tem', 15000.0, 15000.0, 1);
('PH-20251105-0001', 'LO-000003', 50, N'Gần hết HSD (15/12/2025)', 2000.0, 100000.0, 1),
('PH-20251104-0001', 'LO-000002', 1, N'Vỡ chai', 50000.0, 50000.0, 2),
('PH-20251001-0001', 'LO-000005', 1, N'SP đã hết tồn kho', 120000.0, 0.0, 3),
('PH-20240101-0001', 'LO-000006', 2, N'Hỏng bao bì', 10000.0, 20000.0, 2),
('PH-20230501-0001', 'LO-000007', 1, N'Lỗi tem', 15000.0, 15000.0, 1);
GO

-- ===== 6. HÓA ĐƠN (12 HÓA ĐƠN - ĐÃ SỬA ĐỂ KHỚP) =====
INSERT INTO HoaDon (MaHoaDon, MaNhanVien, MaKhachHang, NgayLap, TongTien, TongThanhToan, DiemSuDung, MaKM, SoTienGiamKhuyenMai, ThuocKeDon)
VALUES
-- 1. HD-0001: >500K → KM-20251101-0002 (10%), dùng 1000 điểm
('HD-20251105-0001', 'NV-20251105-0001', 'KH-20251105-0001', '2025-11-05', 1262500.0, 1136250.0 - 1000.0, 1000.0, 'KM-20251101-0002', 126250.0, 1),
-- 2. HD-0002: KM SP (Mua 3 tặng 1 khẩu trang + giảm 500đ/v Amox)
('HD-20251105-0002', 'NV-20251105-0002', 'KH-20000000-0001', '2025-11-05', 108600.0, 108600.0, 0.0, NULL, 0.0, 0),
-- 3. HD-0003: >200K → KM-20251101-0001 (50K)
('HD-20251105-0003', 'NV-20251105-0001', 'KH-20251105-0002', '2025-11-05', 240000.0, 190000.0, 0.0, 'KM-20251101-0001', 50000.0, 0),
-- 4. HD-0004: KM SP Paracetamol 10% + SP5 + SP7
('HD-20251104-0001', 'NV-20251105-0002', 'KH-20251105-0003', '2025-11-04', 238200.0, 238200.0, 0.0, NULL, 0.0, 0),
-- 5. HD-0005: Không KM
('HD-20251103-0001', 'NV-20251105-0001', 'KH-20000000-0001', '2025-11-03', 101500.0, 101500.0, 0.0, NULL, 0.0, 0),
-- 6. HD-0006: <500K → Không KM HĐ
('HD-20251102-0001', 'NV-20251105-0002', 'KH-20251105-0004', '2025-11-02', 208000.0, 208000.0, 0.0, NULL, 0.0, 0),
-- 7. HD-0007: KM hết hạn → Không áp dụng
('HD-20251101-0001', 'NV-20251105-0001', 'KH-20251105-0001', '2025-11-01', 639500.0, 639500.0, 0.0, NULL, 0.0, 0),
-- 8. HD-0008: KM hết hàng → Không áp dụng
('HD-20251030-0001', 'NV-20251105-0002', 'KH-20251105-0002', '2025-10-30', 100000.0, 100000.0, 0.0, NULL, 0.0, 0),
-- 9. HD-0009: Cũ
('HD-20240501-0001', 'NV-20251105-0001', 'KH-20251105-0003', '2024-05-01', 80000.0, 80000.0, 0.0, NULL, 0.0, 0),
-- 10. HD-0010: Cũ
('HD-20230101-0001', 'NV-20251105-0002', 'KH-20251105-0004', '2023-01-01', 50000.0, 50000.0, 0.0, NULL, 0.0, 0),
-- 11. HD-0011: KM SP hết hạn
('HD-20251105-0004', 'NV-20251105-0001', 'KH-20000000-0001', '2025-11-05', 62500.0, 62500.0, 0.0, NULL, 0.0, 0),
-- 12. HD-0012: KM Siro hết hôm nay → ÁP DỤNG (5K)
('HD-20251105-0005', 'NV-20251105-0002', 'KH-20000000-0001', '2025-11-05', 97500.0, 92500.0, 0.0, NULL, 5000.0, 0);
GO

-- ===== CHI TIẾT HÓA ĐƠN (ĐÃ SỬA ĐỦ & KHỚP) =====
INSERT INTO ChiTietHoaDon (MaHoaDon, MaLo, SoLuong, GiaBan, MaKM, ThanhTien)
VALUES
-- HD-0001: 100 viên Paracetamol + 1 Vitamin C → 1,200,000 + 62,500 = 1,262,500 → Giảm 10% = 126,250 → TT: 1,136,250 - 1000đ = 1,135,250
('HD-20251105-0001', 'LO-000001', 100.0, 12000.0, NULL, 1200000.0),
('HD-20251105-0001', 'LO-000002', 1.0, 62500.0, NULL, 62500.0),

-- HD-0002: Mua 5 khẩu trang (tặng 1 → tính 4*2400), Amox 10 viên (giảm 500đ/v → 9900/v)
('HD-20251105-0002', 'LO-000003', 4.0, 2400.0, 'KM-20251101-0006', 9600.0), -- Tặng 1 → chỉ tính tiền 4
('HD-20251105-0002', 'LO-000004', 10.0, 9900.0, 'KM-20251101-0005', 99000.0),

-- HD-0003: 20 viên Paracetamol
('HD-20251105-0003', 'LO-000001', 20.0, 12000.0, NULL, 240000.0),

-- HD-0004: Paracetamol giảm 10% (10 viên), Amox 5 viên, Dầu gió 2 chai
('HD-20251104-0001', 'LO-000001', 10.0, 10800.0, 'KM-20251101-0004', 108000.0),
('HD-20251104-0001', 'LO-000004', 5.0, 10400.0, NULL, 52000.0),
('HD-20251104-0001', 'LO-000007', 2.0, 39000.0, NULL, 78000.0),

-- HD-0005
('HD-20251103-0001', 'LO-000002', 1.0, 62500.0, NULL, 62500.0),
('HD-20251103-0001', 'LO-000007', 1.0, 39000.0, NULL, 39000.0),

-- HD-0006
('HD-20251102-0001', 'LO-000004', 20.0, 10400.0, NULL, 208000.0),

-- HD-0007
('HD-20251101-0001', 'LO-000003', 20.0, 2400.0, NULL, 48000.0),
('HD-20251101-0001', 'LO-000004', 10.0, 10400.0, NULL, 104000.0),
('HD-20251101-0001', 'LO-000006', 5.0, 97500.0, NULL, 487500.0),

-- HD-0011
('HD-20251105-0004', 'LO-000002', 1.0, 62500.0, NULL, 62500.0),

-- HD-0012
('HD-20251105-0005', 'LO-000006', 1.0, 92500.0, 'KM-20251101-0007', 92500.0);
GO

-- ===== PHIẾU TRẢ =====
INSERT INTO PhieuTra (MaPhieuTra, MaKhachHang, MaNhanVien, NgayLap, DaDuyet, TongTienHoan)
VALUES
('PT-20251105-0001', 'KH-20000000-0001', 'NV-20251105-0002', '2025-11-05', 0, 1920.0),
('PT-20251104-0001', 'KH-20251105-0003', 'NV-20251105-0001', '2025-11-04', 1, 108000.0),
('PT-20250501-0001', 'KH-20251105-0004', 'NV-20251105-0002', '2025-05-01', 1, 50000.0);
GO

INSERT INTO ChiTietPhieuTra (MaPhieuTra, MaHoaDon, MaLo, SoLuong, LyDoChiTiet, ThanhTienHoan, TrangThai)
VALUES
('PT-20251105-0001', 'HD-20251105-0002', 'LO-000003', 1, N'Khẩu trang bị lỗi', 1920.0, 0),
('PT-20251104-0001', 'HD-20251104-0001', 'LO-000001', 10, N'Không cần nữa', 108000.0, 1),
('PT-20250501-0001', 'HD-20230101-0001', 'LO-000002', 1, N'Sản phẩm thừa', 50000.0, 2);
GO