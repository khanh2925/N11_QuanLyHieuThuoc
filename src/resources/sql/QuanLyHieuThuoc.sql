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
    HoatDong BIT NOT NULL DEFAULT 1 
    -- ❌ Đã xóa DiemTichLuy
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
    HinhThuc VARCHAR(30) NOT NULL CHECK (HinhThuc IN ('GIAM_GIA_PHAN_TRAM','GIAM_GIA_TIEN')), -- Đã bỏ TANG_THEM
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
    GiaTu FLOAT NOT NULL,
    GiaDen FLOAT NOT NULL,
    TiLe FLOAT NOT NULL CHECK (TiLe > 0 AND TiLe <= 5),
    CONSTRAINT PK_ChiTietBangGia PRIMARY KEY (MaBangGia, GiaTu), 
    CONSTRAINT FK_CTBangGia_BG FOREIGN KEY (MaBangGia) REFERENCES BangGia(MaBangGia),
    CONSTRAINT CHK_GiaRange CHECK (GiaDen >= GiaTu)
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
	MaDonViTinh CHAR(7) NOT NULL,
    TrangThai INT CHECK (TrangThai BETWEEN 1 AND 3),
    CONSTRAINT PK_CTPH PRIMARY KEY (MaPhieuHuy, MaLo),
    CONSTRAINT FK_CTPH_PH FOREIGN KEY (MaPhieuHuy) REFERENCES PhieuHuy(MaPhieuHuy),
	CONSTRAINT FK_CTPH_DVT FOREIGN KEY (MaDonViTinh) REFERENCES DonViTinh(MaDonViTinh),
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
    -- ❌ Đã xóa TongTien
    TongThanhToan FLOAT DEFAULT 0, -- Tổng tiền thực tế khách thanh toán
    -- ❌ Đã xóa DiemSuDung
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
    MaDonViTinh CHAR(7) NOT NULL,           
    SoLuong FLOAT NOT NULL CHECK (SoLuong > 0),
    GiaBan FLOAT NOT NULL CHECK (GiaBan > 0),    
    ThanhTien FLOAT NOT NULL,                    
    MaKM CHAR(17) NULL,                          
    CONSTRAINT PK_CTHD PRIMARY KEY (MaHoaDon, MaLo, MaDonViTinh),
    CONSTRAINT FK_CTHD_HD FOREIGN KEY (MaHoaDon) REFERENCES HoaDon(MaHoaDon) ON DELETE CASCADE,
    CONSTRAINT FK_CTHD_Lo FOREIGN KEY (MaLo) REFERENCES LoSanPham(MaLo),
    CONSTRAINT FK_CTHD_DVT FOREIGN KEY (MaDonViTinh) REFERENCES DonViTinh(MaDonViTinh),
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
    MaPhieuTra CHAR(17) NOT NULL,          -- PT-yyyymmdd-xxxx
    MaHoaDon CHAR(17) NOT NULL,            -- HD-yyyymmdd-xxxx
    MaLo CHAR(9) NOT NULL,                 -- LO-xxxx
    MaDonViTinh CHAR(7) NOT NULL,
    SoLuong INT NOT NULL CHECK (SoLuong > 0),
    LyDoChiTiet NVARCHAR(200),
    ThanhTienHoan FLOAT,
    TrangThai INT CHECK (TrangThai BETWEEN 0 AND 2),
    CONSTRAINT PK_CTPTRA PRIMARY KEY (MaPhieuTra, MaHoaDon, MaLo, MaDonViTinh),
    CONSTRAINT FK_CTPTRA_PT FOREIGN KEY (MaPhieuTra) REFERENCES PhieuTra(MaPhieuTra),
    CONSTRAINT FK_CTPTRA_HD FOREIGN KEY (MaHoaDon) REFERENCES HoaDon(MaHoaDon),
    CONSTRAINT FK_CTPTRA_LO FOREIGN KEY (MaLo) REFERENCES LoSanPham(MaLo),
    CONSTRAINT FK_CTPTRA_DVT FOREIGN KEY (MaDonViTinh) REFERENCES DonViTinh(MaDonViTinh)
);
GO


------------------------------------------------------------
-- 6. KHÁC: CHI TIẾT KHUYẾN MÃI - SẢN PHẨM
------------------------------------------------------------
CREATE TABLE ChiTietKhuyenMaiSanPham (
    MaKM CHAR(17) NOT NULL,
    MaSanPham CHAR(9) NOT NULL,
    CONSTRAINT PK_CTKMSP PRIMARY KEY (MaKM, MaSanPham),
    CONSTRAINT FK_CTKMSP_KM FOREIGN KEY (MaKM) REFERENCES KhuyenMai(MaKM),
    CONSTRAINT FK_CTKMSP_SP FOREIGN KEY (MaSanPham) REFERENCES SanPham(MaSanPham)
);
GO