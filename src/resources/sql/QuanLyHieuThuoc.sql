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
    MaNhanVien CHAR(18) PRIMARY KEY CHECK (MaNhanVien LIKE 'NV-[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]-[0-9][0-9][0-9][0-9]'),
    TenNhanVien NVARCHAR(50) NOT NULL CHECK (LEN(RTRIM(LTRIM(TenNhanVien))) > 0 AND LEN(TenNhanVien) <= 50),
    GioiTinh BIT NOT NULL, 
    NgaySinh DATE NOT NULL CHECK (DATEDIFF(YEAR, NgaySinh, GETDATE()) >= 18),
    SoDienThoai CHAR(10) NOT NULL CHECK (SoDienThoai LIKE '0[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]'),
    DiaChi NVARCHAR(100) NOT NULL CHECK (LEN(RTRIM(LTRIM(DiaChi))) > 0 AND LEN(DiaChi) <= 100),
    QuanLy BIT NOT NULL DEFAULT 0, 
    CaLam TINYINT NOT NULL CHECK (CaLam BETWEEN 1 AND 3), 
    TrangThai BIT NOT NULL DEFAULT 1 
);
GO

CREATE TABLE TaiKhoan (
    MaTaiKhoan CHAR(18) PRIMARY KEY CHECK (MaTaiKhoan LIKE 'TK-[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]-[0-9][0-9][0-9][0-9]'),
    TenDangNhap VARCHAR(30) NOT NULL UNIQUE CHECK (LEN(TenDangNhap) BETWEEN 5 AND 30 AND TenDangNhap NOT LIKE '% %'),
    MatKhau VARCHAR(100) NOT NULL CHECK (LEN(MatKhau) >= 8),
    MaNhanVien CHAR(18) NOT NULL,
    CONSTRAINT FK_TaiKhoan_NhanVien FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien)
);
GO

CREATE TABLE KhachHang (
    MaKhachHang CHAR(18) PRIMARY KEY CHECK (MaKhachHang LIKE 'KH-[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]-[0-9][0-9][0-9][0-9]'),
    TenKhachHang NVARCHAR(100) NOT NULL CHECK (LEN(RTRIM(LTRIM(TenKhachHang))) > 0 AND LEN(TenKhachHang) <= 100),
    GioiTinh BIT NOT NULL, 
    SoDienThoai CHAR(10) NOT NULL CHECK (SoDienThoai LIKE '0[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]'),
    NgaySinh DATE NOT NULL CHECK (NgaySinh <= GETDATE() AND DATEDIFF(YEAR, NgaySinh, GETDATE()) >= 16),
    HoatDong BIT NOT NULL DEFAULT 1 
);
GO

------------------------------------------------------------
-- NHÀ CUNG CẤP
------------------------------------------------------------
CREATE TABLE NhaCungCap (
    MaNhaCungCap CHAR(19) PRIMARY KEY CHECK (MaNhaCungCap LIKE 'NCC-[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]-[0-9][0-9][0-9][0-9]'),
    TenNhaCungCap NVARCHAR(100) NOT NULL CHECK (LEN(RTRIM(LTRIM(TenNhaCungCap))) > 0 AND LEN(TenNhaCungCap) <= 100),
    SoDienThoai CHAR(10) NOT NULL CHECK (SoDienThoai LIKE '0[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]'),
    DiaChi NVARCHAR(200) NOT NULL CHECK (LEN(RTRIM(LTRIM(DiaChi))) > 0 AND LEN(DiaChi) <= 200),
    Email NVARCHAR(100),
    HoatDong BIT NOT NULL DEFAULT 1
);
GO

------------------------------------------------------------
-- BẢNG GIÁ
------------------------------------------------------------
CREATE TABLE BangGia (
    MaBangGia CHAR(18) PRIMARY KEY CHECK (MaBangGia LIKE 'BG-[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]-[0-9][0-9][0-9][0-9]'),
    MaNhanVien CHAR(18) NOT NULL,
    TenBangGia NVARCHAR(100) NOT NULL CHECK (LEN(RTRIM(LTRIM(TenBangGia))) > 0 AND LEN(TenBangGia) <= 100),
    NgayApDung DATE NOT NULL CHECK (NgayApDung >= '2000-01-01'),
    HoatDong BIT NOT NULL DEFAULT 1,
    CONSTRAINT FK_BangGia_NhanVien FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien)
);
GO

------------------------------------------------------------
-- KHUYẾN MÃI (✅ SỬA: TABLE-LEVEL CONSTRAINT)
------------------------------------------------------------
CREATE TABLE KhuyenMai (
    MaKM CHAR(18) PRIMARY KEY CHECK (MaKM LIKE 'KM-[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]-[0-9][0-9][0-9][0-9]'),
    TenKM NVARCHAR(200) NOT NULL CHECK (LEN(RTRIM(LTRIM(TenKM))) > 0 AND LEN(TenKM) <= 200),
    NgayBatDau DATE NOT NULL,
    NgayKetThuc DATE NOT NULL,
    TrangThai BIT NOT NULL,
    KhuyenMaiHoaDon BIT NOT NULL, 
    HinhThuc VARCHAR(30) NOT NULL CHECK (HinhThuc IN ('GIAM_GIA_PHAN_TRAM','GIAM_GIA_TIEN')),
    GiaTri FLOAT NOT NULL CHECK (GiaTri >= 0),
    DieuKienApDungHoaDon FLOAT DEFAULT 0 CHECK (DieuKienApDungHoaDon >= 0), 
    SoLuongKhuyenMai INT DEFAULT 0 CHECK (SoLuongKhuyenMai >= 0),
    -- ✅ Table-level constraint
    CONSTRAINT CHK_KM_NgayKetThuc CHECK (NgayKetThuc >= NgayBatDau)
);
GO

------------------------------------------------------------
-- SẢN PHẨM & QUY CÁCH
------------------------------------------------------------
CREATE TABLE SanPham (
    MaSanPham CHAR(9) PRIMARY KEY CHECK (MaSanPham LIKE 'SP-[0-9][0-9][0-9][0-9][0-9][0-9]'),
    TenSanPham NVARCHAR(100) NOT NULL CHECK (LEN(RTRIM(LTRIM(TenSanPham))) > 0 AND LEN(TenSanPham) <= 100),
    LoaiSanPham VARCHAR(50) NOT NULL CHECK (LoaiSanPham IN 
        ('THUOC','THUC_PHAM_BO_SUNG','MY_PHAM','DUNG_CU_Y_TE','SAN_PHAM_CHO_ME_VA_BE','SAN_PHAM_KHAC')),
    SoDangKy VARCHAR(20),
    DuongDung VARCHAR(20) CHECK (DuongDung IN ('UONG','TIEM','NHO','BOI','HIT','NGAM','DAT','DAN', 'KHAC')),
    GiaNhap FLOAT NOT NULL CHECK (GiaNhap > 0),
    GiaBan FLOAT CHECK (GiaBan >= 0),
    HinhAnh NVARCHAR(255),
    KeBanSanPham NVARCHAR(100), 
    HoatDong BIT NOT NULL DEFAULT 1
);
GO

CREATE TABLE LoSanPham (
    MaLo CHAR(9) PRIMARY KEY CHECK (MaLo LIKE 'LO-[0-9][0-9][0-9][0-9][0-9][0-9]'),
    HanSuDung DATE NOT NULL CHECK (HanSuDung >= '1975-01-01'),
    SoLuongTon INT DEFAULT 0 CHECK (SoLuongTon >= 0),
    MaSanPham CHAR(9) NOT NULL,
    CONSTRAINT FK_LoSanPham_SanPham FOREIGN KEY (MaSanPham) REFERENCES SanPham(MaSanPham)
);
GO

CREATE TABLE QuyCachDongGoi (
    MaQuyCach CHAR(9) PRIMARY KEY CHECK (MaQuyCach LIKE 'QC-[0-9][0-9][0-9][0-9][0-9][0-9]'),
    MaDonViTinh CHAR(7) NOT NULL,
    MaSanPham CHAR(9) NOT NULL,
    HeSoQuyDoi INT NOT NULL CHECK (HeSoQuyDoi > 0),
    TiLeGiam FLOAT CHECK (TiLeGiam BETWEEN 0 AND 1),
    DonViGoc BIT NOT NULL DEFAULT 0,
    TrangThai BIT NOT NULL DEFAULT 1,
    CONSTRAINT FK_QC_DVT FOREIGN KEY (MaDonViTinh) REFERENCES DonViTinh(MaDonViTinh),
    CONSTRAINT FK_QC_SP FOREIGN KEY (MaSanPham) REFERENCES SanPham(MaSanPham)
);
GO

CREATE TABLE ChiTietBangGia (
    MaBangGia CHAR(18) NOT NULL,    
    GiaTu FLOAT NOT NULL CHECK (GiaTu >= 0),
    GiaDen FLOAT NOT NULL,
    TiLe FLOAT NOT NULL CHECK (TiLe > 0 AND TiLe <= 5),
    CONSTRAINT PK_ChiTietBangGia PRIMARY KEY (MaBangGia, GiaTu), 
    CONSTRAINT FK_CTBangGia_BG FOREIGN KEY (MaBangGia) REFERENCES BangGia(MaBangGia),
    -- ✅ Table-level constraint
    CONSTRAINT CHK_CTBG_GiaRange CHECK (GiaDen >= GiaTu)
);
GO

------------------------------------------------------------
-- NGHIỆP VỤ NHẬP HÀNG & HỦY HÀNG
------------------------------------------------------------
CREATE TABLE PhieuNhap (
    MaPhieuNhap CHAR(18) PRIMARY KEY CHECK (MaPhieuNhap LIKE 'PN-[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]-[0-9][0-9][0-9][0-9]'),
    NgayNhap DATE NOT NULL CHECK (NgayNhap <= GETDATE()),
    MaNhaCungCap CHAR(19) NOT NULL,
    MaNhanVien CHAR(18) NOT NULL,
    TongTien FLOAT DEFAULT 0 CHECK (TongTien >= 0),
    CONSTRAINT FK_PN_NCC FOREIGN KEY (MaNhaCungCap) REFERENCES NhaCungCap(MaNhaCungCap),
    CONSTRAINT FK_PN_NV FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien)
);
GO

CREATE TABLE ChiTietPhieuNhap (
    MaPhieuNhap CHAR(18) NOT NULL,
    MaLo CHAR(9) NOT NULL,
    MaDonViTinh CHAR(7) NOT NULL,
    SoLuongNhap INT NOT NULL CHECK (SoLuongNhap > 0),
    DonGiaNhap FLOAT NOT NULL CHECK (DonGiaNhap > 0),
    ThanhTien FLOAT CHECK (ThanhTien >= 0),
    CONSTRAINT PK_CTPN PRIMARY KEY (MaPhieuNhap, MaLo),
    CONSTRAINT FK_CTPN_PN FOREIGN KEY (MaPhieuNhap) REFERENCES PhieuNhap(MaPhieuNhap),
    CONSTRAINT FK_CTPN_Lo FOREIGN KEY (MaLo) REFERENCES LoSanPham(MaLo),
    CONSTRAINT FK_CTPN_DVT FOREIGN KEY (MaDonViTinh) REFERENCES DonViTinh(MaDonViTinh)
);
GO

CREATE TABLE PhieuHuy (
    MaPhieuHuy CHAR(18) PRIMARY KEY CHECK (MaPhieuHuy LIKE 'PH-[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]-[0-9][0-9][0-9][0-9]'),
    NgayLapPhieu DATE NOT NULL CHECK (NgayLapPhieu <= GETDATE()),
    MaNhanVien CHAR(18) NOT NULL,
    TrangThai BIT NOT NULL,
    TongTien FLOAT DEFAULT 0 CHECK (TongTien >= 0),
    CONSTRAINT FK_PH_NV FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien)
);
GO

CREATE TABLE ChiTietPhieuHuy (
    MaPhieuHuy CHAR(18) NOT NULL,
    MaLo CHAR(9) NOT NULL,
    SoLuongHuy INT NOT NULL CHECK (SoLuongHuy > 0),
    LyDoChiTiet NVARCHAR(500),
    DonGiaNhap FLOAT NOT NULL CHECK (DonGiaNhap > 0),
    ThanhTien FLOAT CHECK (ThanhTien >= 0),
    MaDonViTinh CHAR(7) NOT NULL,
    TrangThai INT CHECK (TrangThai BETWEEN 1 AND 3),
    CONSTRAINT PK_CTPH PRIMARY KEY (MaPhieuHuy, MaLo),
    CONSTRAINT FK_CTPH_PH FOREIGN KEY (MaPhieuHuy) REFERENCES PhieuHuy(MaPhieuHuy),
    CONSTRAINT FK_CTPH_DVT FOREIGN KEY (MaDonViTinh) REFERENCES DonViTinh(MaDonViTinh),
    CONSTRAINT FK_CTPH_Lo FOREIGN KEY (MaLo) REFERENCES LoSanPham(MaLo)
);
GO

------------------------------------------------------------
-- NGHIỆP VỤ BÁN HÀNG & TRẢ HÀNG
------------------------------------------------------------
CREATE TABLE HoaDon (
    MaHoaDon CHAR(18) PRIMARY KEY CHECK (MaHoaDon LIKE 'HD-[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]-[0-9][0-9][0-9][0-9]'),
    MaNhanVien CHAR(18) NOT NULL,
    MaKhachHang CHAR(18) NOT NULL,
    NgayLap DATE NOT NULL CHECK (NgayLap <= GETDATE()),
    TongThanhToan FLOAT DEFAULT 0 CHECK (TongThanhToan >= 0),
    MaKM CHAR(18) NULL,
    SoTienGiamKhuyenMai FLOAT DEFAULT 0 CHECK (SoTienGiamKhuyenMai >= 0), 
    ThuocKeDon BIT NOT NULL DEFAULT 0,
    CONSTRAINT FK_HD_NV FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien),
    CONSTRAINT FK_HD_KH FOREIGN KEY (MaKhachHang) REFERENCES KhachHang(MaKhachHang),
    CONSTRAINT FK_HD_KM FOREIGN KEY (MaKM) REFERENCES KhuyenMai(MaKM)
);
GO

CREATE TABLE ChiTietHoaDon (
    MaHoaDon CHAR(18) NOT NULL,
    MaLo CHAR(9) NOT NULL,
    MaDonViTinh CHAR(7) NOT NULL,           
    SoLuong FLOAT NOT NULL CHECK (SoLuong > 0),
    GiaBan FLOAT NOT NULL CHECK (GiaBan > 0),    
    ThanhTien FLOAT NOT NULL CHECK (ThanhTien >= 0),                    
    MaKM CHAR(18) NULL,                          
    CONSTRAINT PK_CTHD PRIMARY KEY (MaHoaDon, MaLo, MaDonViTinh),
    CONSTRAINT FK_CTHD_HD FOREIGN KEY (MaHoaDon) REFERENCES HoaDon(MaHoaDon) ON DELETE CASCADE,
    CONSTRAINT FK_CTHD_Lo FOREIGN KEY (MaLo) REFERENCES LoSanPham(MaLo),
    CONSTRAINT FK_CTHD_DVT FOREIGN KEY (MaDonViTinh) REFERENCES DonViTinh(MaDonViTinh),
    CONSTRAINT FK_CTHD_KM FOREIGN KEY (MaKM) REFERENCES KhuyenMai(MaKM)
);
GO

CREATE TABLE PhieuTra (
    MaPhieuTra CHAR(18) PRIMARY KEY CHECK (MaPhieuTra LIKE 'PT-[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]-[0-9][0-9][0-9][0-9]'),
    MaKhachHang CHAR(18) NOT NULL,
    MaNhanVien CHAR(18) NOT NULL,
    NgayLap DATE NOT NULL CHECK (NgayLap <= GETDATE()),
    DaDuyet BIT DEFAULT 0,
    TongTienHoan FLOAT DEFAULT 0 CHECK (TongTienHoan >= 0),
    CONSTRAINT FK_PT_KH FOREIGN KEY (MaKhachHang) REFERENCES KhachHang(MaKhachHang),
    CONSTRAINT FK_PT_NV FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien)
);
GO

CREATE TABLE ChiTietPhieuTra (
    MaPhieuTra CHAR(18) NOT NULL,
    MaHoaDon CHAR(18) NOT NULL,
    MaLo CHAR(9) NOT NULL,
    MaDonViTinh CHAR(7) NOT NULL,
    SoLuong INT NOT NULL CHECK (SoLuong > 0),
    LyDoChiTiet NVARCHAR(200),
    ThanhTienHoan FLOAT CHECK (ThanhTienHoan >= 0),
    TrangThai INT CHECK (TrangThai BETWEEN 0 AND 2),
    CONSTRAINT PK_CTPTRA PRIMARY KEY (MaPhieuTra, MaHoaDon, MaLo, MaDonViTinh),
    CONSTRAINT FK_CTPTRA_PT FOREIGN KEY (MaPhieuTra) REFERENCES PhieuTra(MaPhieuTra),
    CONSTRAINT FK_CTPTRA_HD FOREIGN KEY (MaHoaDon) REFERENCES HoaDon(MaHoaDon),
    CONSTRAINT FK_CTPTRA_LO FOREIGN KEY (MaLo) REFERENCES LoSanPham(MaLo),
    CONSTRAINT FK_CTPTRA_DVT FOREIGN KEY (MaDonViTinh) REFERENCES DonViTinh(MaDonViTinh)
);
GO

------------------------------------------------------------
-- CHI TIẾT KHUYẾN MÃI - SẢN PHẨM
------------------------------------------------------------
CREATE TABLE ChiTietKhuyenMaiSanPham (
    MaKM CHAR(18) NOT NULL,
    MaSanPham CHAR(9) NOT NULL,
    CONSTRAINT PK_CTKMSP PRIMARY KEY (MaKM, MaSanPham),
    CONSTRAINT FK_CTKMSP_KM FOREIGN KEY (MaKM) REFERENCES KhuyenMai(MaKM),
    CONSTRAINT FK_CTKMSP_SP FOREIGN KEY (MaSanPham) REFERENCES SanPham(MaSanPham)
);
GO
