USE QuanLyHieuThuoc
GO
-- 10. KHUYENMAI

INSERT INTO KhuyenMai (
    maKM, tenKM, ngayBatDau, ngayKetThuc, trangThai, 
    khuyenMaiHoaDon, hinhThuc, giaTri, dieuKienApDungHoaDon, soLuongKhuyenMai
)
VALUES
-- ================================================================
-- KHUYẾN MÃI HÓA ĐƠN - GIẢM GIÁ PHẦN TRĂM (20 KM)
-- ================================================================
-- Đang hoạt động (trangThai = 1)
('KM-20251201-0001', N'Giảm 5% toàn bộ hóa đơn từ 100K', '2025-12-01', '2025-12-31', 1, 1, 'GIAM_GIA_PHAN_TRAM', 5, 100000, 500),
('KM-20251201-0002', N'Giảm 8% toàn bộ hóa đơn từ 200K', '2025-12-01', '2025-12-31', 1, 1, 'GIAM_GIA_PHAN_TRAM', 8, 200000, 450),
('KM-20251201-0003', N'Giảm 10% toàn bộ hóa đơn từ 300K', '2025-12-01', '2025-12-31', 1, 1, 'GIAM_GIA_PHAN_TRAM', 10, 300000, 400),
('KM-20251201-0004', N'Giảm 12% toàn bộ hóa đơn từ 400K', '2025-12-01', '2025-12-31', 1, 1, 'GIAM_GIA_PHAN_TRAM', 12, 400000, 350),
('KM-20251201-0005', N'Giảm 15% toàn bộ hóa đơn từ 500K', '2025-12-01', '2025-12-31', 1, 1, 'GIAM_GIA_PHAN_TRAM', 15, 500000, 300),
('KM-20251201-0006', N'Giảm 18% toàn bộ hóa đơn từ 700K', '2025-12-01', '2025-12-31', 1, 1, 'GIAM_GIA_PHAN_TRAM', 18, 700000, 250),
('KM-20251201-0007', N'Giảm 20% toàn bộ hóa đơn từ 1 triệu', '2025-12-01', '2025-12-31', 1, 1, 'GIAM_GIA_PHAN_TRAM', 20, 1000000, 200),
('KM-20251201-0008', N'Giảm 22% toàn bộ hóa đơn từ 1.5 triệu', '2025-12-01', '2025-12-31', 1, 1, 'GIAM_GIA_PHAN_TRAM', 22, 1500000, 150),
('KM-20251201-0009', N'Giảm 25% toàn bộ hóa đơn từ 2 triệu', '2025-12-01', '2025-12-31', 1, 1, 'GIAM_GIA_PHAN_TRAM', 25, 2000000, 100),
('KM-20251201-0010', N'Giảm 30% toàn bộ hóa đơn từ 3 triệu', '2025-12-01', '2025-12-31', 1, 1, 'GIAM_GIA_PHAN_TRAM', 30, 3000000, 50),
-- Đã kết thúc (trangThai = 0)
('KM-20251101-0011', N'Black Friday - Giảm 10% đơn từ 200K', '2025-11-01', '2025-11-30', 0, 1, 'GIAM_GIA_PHAN_TRAM', 10, 200000, 0),
('KM-20251101-0012', N'Black Friday - Giảm 15% đơn từ 500K', '2025-11-01', '2025-11-30', 0, 1, 'GIAM_GIA_PHAN_TRAM', 15, 500000, 0),
('KM-20251101-0013', N'Black Friday - Giảm 20% đơn từ 1 triệu', '2025-11-01', '2025-11-30', 0, 1, 'GIAM_GIA_PHAN_TRAM', 20, 1000000, 0),
('KM-20251001-0014', N'Tháng 10 - Giảm 8% đơn từ 150K', '2025-10-01', '2025-10-31', 0, 1, 'GIAM_GIA_PHAN_TRAM', 8, 150000, 0),
('KM-20251001-0015', N'Tháng 10 - Giảm 12% đơn từ 300K', '2025-10-01', '2025-10-31', 0, 1, 'GIAM_GIA_PHAN_TRAM', 12, 300000, 0),
-- Chưa bắt đầu (trangThai = 2)
('KM-20260101-0016', N'Tết 2026 - Giảm 15% đơn từ 500K', '2026-01-15', '2026-02-15', 2, 1, 'GIAM_GIA_PHAN_TRAM', 15, 500000, 600),
('KM-20260101-0017', N'Tết 2026 - Giảm 20% đơn từ 1 triệu', '2026-01-15', '2026-02-15', 2, 1, 'GIAM_GIA_PHAN_TRAM', 20, 1000000, 400),
('KM-20260101-0018', N'Tết 2026 - Giảm 25% đơn từ 2 triệu', '2026-01-15', '2026-02-15', 2, 1, 'GIAM_GIA_PHAN_TRAM', 25, 2000000, 200),
('KM-20260101-0019', N'Valentine 2026 - Giảm 14% đơn từ 300K', '2026-02-10', '2026-02-16', 2, 1, 'GIAM_GIA_PHAN_TRAM', 14, 300000, 300),
('KM-20260101-0020', N'Valentine 2026 - Giảm 20% đơn từ 800K', '2026-02-10', '2026-02-16', 2, 1, 'GIAM_GIA_PHAN_TRAM', 20, 800000, 150),

-- ================================================================
-- KHUYẾN MÃI HÓA ĐƠN - GIẢM GIÁ TIỀN (20 KM)
-- ================================================================
-- Đang hoạt động (trangThai = 1)
('KM-20251201-0021', N'Giảm 10K cho đơn từ 100K', '2025-12-01', '2025-12-31', 1, 1, 'GIAM_GIA_TIEN', 10000, 100000, 500),
('KM-20251201-0022', N'Giảm 20K cho đơn từ 200K', '2025-12-01', '2025-12-31', 1, 1, 'GIAM_GIA_TIEN', 20000, 200000, 450),
('KM-20251201-0023', N'Giảm 30K cho đơn từ 300K', '2025-12-01', '2025-12-31', 1, 1, 'GIAM_GIA_TIEN', 30000, 300000, 400),
('KM-20251201-0024', N'Giảm 50K cho đơn từ 500K', '2025-12-01', '2025-12-31', 1, 1, 'GIAM_GIA_TIEN', 50000, 500000, 350),
('KM-20251201-0025', N'Giảm 80K cho đơn từ 800K', '2025-12-01', '2025-12-31', 1, 1, 'GIAM_GIA_TIEN', 80000, 800000, 300),
('KM-20251201-0026', N'Giảm 100K cho đơn từ 1 triệu', '2025-12-01', '2025-12-31', 1, 1, 'GIAM_GIA_TIEN', 100000, 1000000, 250),
('KM-20251201-0027', N'Giảm 150K cho đơn từ 1.5 triệu', '2025-12-01', '2025-12-31', 1, 1, 'GIAM_GIA_TIEN', 150000, 1500000, 200),
('KM-20251201-0028', N'Giảm 200K cho đơn từ 2 triệu', '2025-12-01', '2025-12-31', 1, 1, 'GIAM_GIA_TIEN', 200000, 2000000, 150),
('KM-20251201-0029', N'Giảm 300K cho đơn từ 3 triệu', '2025-12-01', '2025-12-31', 1, 1, 'GIAM_GIA_TIEN', 300000, 3000000, 100),
('KM-20251201-0030', N'Giảm 500K cho đơn từ 5 triệu', '2025-12-01', '2025-12-31', 1, 1, 'GIAM_GIA_TIEN', 500000, 5000000, 50),
-- Đã kết thúc (trangThai = 0)
('KM-20251101-0031', N'Tháng 11 - Giảm 15K đơn từ 150K', '2025-11-01', '2025-11-30', 0, 1, 'GIAM_GIA_TIEN', 15000, 150000, 0),
('KM-20251101-0032', N'Tháng 11 - Giảm 25K đơn từ 250K', '2025-11-01', '2025-11-30', 0, 1, 'GIAM_GIA_TIEN', 25000, 250000, 0),
('KM-20251101-0033', N'Tháng 11 - Giảm 40K đơn từ 400K', '2025-11-01', '2025-11-30', 0, 1, 'GIAM_GIA_TIEN', 40000, 400000, 0),
('KM-20251001-0034', N'Tháng 10 - Giảm 12K đơn từ 120K', '2025-10-01', '2025-10-31', 0, 1, 'GIAM_GIA_TIEN', 12000, 120000, 0),
('KM-20251001-0035', N'Tháng 10 - Giảm 35K đơn từ 350K', '2025-10-01', '2025-10-31', 0, 1, 'GIAM_GIA_TIEN', 35000, 350000, 0),
-- Chưa bắt đầu (trangThai = 2)
('KM-20260101-0036', N'Tết 2026 - Giảm 50K đơn từ 500K', '2026-01-15', '2026-02-15', 2, 1, 'GIAM_GIA_TIEN', 50000, 500000, 500),
('KM-20260101-0037', N'Tết 2026 - Giảm 100K đơn từ 1 triệu', '2026-01-15', '2026-02-15', 2, 1, 'GIAM_GIA_TIEN', 100000, 1000000, 300),
('KM-20260101-0038', N'Tết 2026 - Giảm 200K đơn từ 2 triệu', '2026-01-15', '2026-02-15', 2, 1, 'GIAM_GIA_TIEN', 200000, 2000000, 150),
('KM-20260101-0039', N'8/3 - Giảm 30K đơn từ 300K', '2026-03-01', '2026-03-10', 2, 1, 'GIAM_GIA_TIEN', 30000, 300000, 400),
('KM-20260101-0040', N'8/3 - Giảm 80K đơn từ 800K', '2026-03-01', '2026-03-10', 2, 1, 'GIAM_GIA_TIEN', 80000, 800000, 200),

-- ================================================================
-- KHUYẾN MÃI SẢN PHẨM (10 KM)
-- khuyenMaiHoaDon = 0, dieuKienApDungHoaDon = 0
-- ================================================================
-- Đang hoạt động (trangThai = 1)
('KM-20251201-0041', N'Giảm 10% Thuốc kháng sinh', '2025-12-01', '2025-12-31', 1, 0, 'GIAM_GIA_PHAN_TRAM', 10, 0, 200),
('KM-20251201-0042', N'Giảm 15% Vitamin và thực phẩm bổ sung', '2025-12-01', '2025-12-31', 1, 0, 'GIAM_GIA_PHAN_TRAM', 15, 0, 180),
('KM-20251201-0043', N'Giảm 12% Sữa bột và dinh dưỡng cho bé', '2025-12-01', '2025-12-31', 1, 0, 'GIAM_GIA_PHAN_TRAM', 12, 0, 160),
('KM-20251201-0044', N'Giảm 20% Dụng cụ y tế', '2025-12-01', '2025-12-31', 1, 0, 'GIAM_GIA_PHAN_TRAM', 20, 0, 140),
('KM-20251201-0045', N'Giảm 18% Mỹ phẩm dược liệu', '2025-12-01', '2025-12-31', 1, 0, 'GIAM_GIA_PHAN_TRAM', 18, 0, 120),
-- Đã kết thúc (trangThai = 0)
('KM-20251101-0046', N'Tháng 11 - Giảm 15% Thuốc ho và cảm cúm', '2025-11-01', '2025-11-30', 0, 0, 'GIAM_GIA_PHAN_TRAM', 15, 0, 0),
('KM-20251101-0047', N'Tháng 11 - Giảm 20% Thuốc tiêu hóa', '2025-11-01', '2025-11-30', 0, 0, 'GIAM_GIA_PHAN_TRAM', 20, 0, 0),
-- Chưa bắt đầu (trangThai = 2)
('KM-20260101-0048', N'Tết 2026 - Giảm 25% Thuốc bổ não và tim mạch', '2026-01-15', '2026-02-15', 2, 0, 'GIAM_GIA_PHAN_TRAM', 25, 0, 250),
('KM-20260101-0049', N'Tết 2026 - Giảm 20% Sản phẩm chăm sóc da', '2026-01-15', '2026-02-15', 2, 0, 'GIAM_GIA_PHAN_TRAM', 20, 0, 200),
('KM-20260101-0050', N'Tết 2026 - Giảm 30% Thuốc giảm đau và kháng viêm', '2026-01-15', '2026-02-15', 2, 0, 'GIAM_GIA_PHAN_TRAM', 30, 0, 180);
GO

-- 11. CHI TIẾT KHUYẾN MÃI SẢN PHẨM
INSERT INTO ChiTietKhuyenMaiSanPham (maKM, maSanPham) VALUES
-- KM-20251201-0041: Giảm 10% Thuốc kháng sinh (10 SP)
('KM-20251201-0041', 'SP-000009'), -- Amoxicillin 250mg
('KM-20251201-0041', 'SP-000023'), -- Amoxicillin 250mg
('KM-20251201-0041', 'SP-000030'), -- Amoxicillin 250mg
('KM-20251201-0041', 'SP-000032'), -- Augmentin 625mg
('KM-20251201-0041', 'SP-000033'), -- Cefixime 200mg
('KM-20251201-0041', 'SP-000034'), -- Azithromycin 500mg
('KM-20251201-0041', 'SP-000035'), -- Ciprofloxacin 500mg
('KM-20251201-0041', 'SP-000131'), -- Thuốc trị nấm da Canesten
('KM-20251201-0041', 'SP-000132'), -- Thuốc mỡ Fucidin
('KM-20251201-0041', 'SP-000133'), -- Thuốc nhỏ mắt Tobrex

-- KM-20251201-0042: Giảm 15% Vitamin và thực phẩm bổ sung (14 SP)
('KM-20251201-0042', 'SP-000001'), -- Vitamin tổng hợp Centrum
('KM-20251201-0042', 'SP-000012'), -- Vitamin C 1000mg
('KM-20251201-0042', 'SP-000027'), -- Men tiêu hóa Enterogermina
('KM-20251201-0042', 'SP-000066'), -- Vitamin B Complex
('KM-20251201-0042', 'SP-000067'), -- Vitamin E 400IU
('KM-20251201-0042', 'SP-000068'), -- Vitamin D3 1000IU
('KM-20251201-0042', 'SP-000069'), -- Canxi D3
('KM-20251201-0042', 'SP-000070'), -- Sắt Folic
('KM-20251201-0042', 'SP-000071'), -- Omega 3 Fish Oil
('KM-20251201-0042', 'SP-000072'), -- Glucosamine 1500mg
('KM-20251201-0042', 'SP-000073'), -- Collagen Type 1
('KM-20251201-0042', 'SP-000074'), -- Probiotics 10 tỷ CFU
('KM-20251201-0042', 'SP-000076'), -- Kẽm Zinc
('KM-20251201-0042', 'SP-000077'), -- Magie B6

-- KM-20251201-0043: Giảm 12% Sữa bột và dinh dưỡng cho bé (12 SP)
('KM-20251201-0043', 'SP-000005'), -- Sữa bột Ensure
('KM-20251201-0043', 'SP-000008'), -- Sữa bột Dielac
('KM-20251201-0043', 'SP-000029'), -- Sữa bột Ensure
('KM-20251201-0043', 'SP-000081'), -- Sữa bột Similac
('KM-20251201-0043', 'SP-000082'), -- Sữa bột Nan
('KM-20251201-0043', 'SP-000083'), -- Sữa bột Enfamil
('KM-20251201-0043', 'SP-000084'), -- Sữa bột Friso
('KM-20251201-0043', 'SP-000085'), -- Sữa bột Meiji
('KM-20251201-0043', 'SP-000211'), -- Sữa bột Aptamil
('KM-20251201-0043', 'SP-000212'), -- Sữa bột S26
('KM-20251201-0043', 'SP-000213'), -- Sữa bột Grow Plus
('KM-20251201-0043', 'SP-000214'), -- Sữa bột Pediasure

-- KM-20251201-0044: Giảm 20% Dụng cụ y tế (11 SP)
('KM-20251201-0044', 'SP-000007'), -- Dụng cụ hút sữa
('KM-20251201-0044', 'SP-000014'), -- Khẩu trang y tế 4 lớp
('KM-20251201-0044', 'SP-000028'), -- Máy đo huyết áp Omron
('KM-20251201-0044', 'SP-000096'), -- Nhiệt kế điện tử Omron
('KM-20251201-0044', 'SP-000097'), -- Máy đo đường huyết
('KM-20251201-0044', 'SP-000098'), -- Que thử đường huyết
('KM-20251201-0044', 'SP-000099'), -- Máy xông khí dung
('KM-20251201-0044', 'SP-000231'), -- Máy đo SpO2
('KM-20251201-0044', 'SP-000232'), -- Máy đo huyết áp cổ tay
('KM-20251201-0044', 'SP-000331'), -- Máy đo nhịp tim
('KM-20251201-0044', 'SP-000335'), -- Cân sức khỏe điện tử

-- KM-20251201-0045: Giảm 18% Mỹ phẩm dược liệu (10 SP)
('KM-20251201-0045', 'SP-000006'), -- Kem trị mụn Acnes
('KM-20251201-0045', 'SP-000013'), -- Son dưỡng môi Vaseline
('KM-20251201-0045', 'SP-000016'), -- Kem chống nắng SPF50+
('KM-20251201-0045', 'SP-000020'), -- Dầu gội Head & Shoulders
('KM-20251201-0045', 'SP-000111'), -- Kem dưỡng ẩm Nivea
('KM-20251201-0045', 'SP-000112'), -- Sữa rửa mặt Cetaphil
('KM-20251201-0045', 'SP-000113'), -- Serum Vitamin C
('KM-20251201-0045', 'SP-000241'), -- Kem dưỡng da La Roche-Posay
('KM-20251201-0045', 'SP-000242'), -- Sữa rửa mặt La Roche-Posay
('KM-20251201-0045', 'SP-000243'), -- Kem chống nắng La Roche-Posay

-- KM-20251101-0046: Thuốc ho và cảm cúm (8 SP)
('KM-20251101-0046', 'SP-000010'), -- Siro ho trẻ em
('KM-20251101-0046', 'SP-000015'), -- Efferalgan
('KM-20251101-0046', 'SP-000017'), -- Decolgen
('KM-20251101-0046', 'SP-000024'), -- Thuốc cảm cúm Coldacmin
('KM-20251101-0046', 'SP-000026'), -- Thuốc ho Prospan
('KM-20251101-0046', 'SP-000143'), -- Siro ho Bisolvon
('KM-20251101-0046', 'SP-000144'), -- Siro ho Mucosolvan
('KM-20251101-0046', 'SP-000145'), -- Siro ho Astex

-- KM-20251101-0047: Thuốc tiêu hóa (9 SP)
('KM-20251101-0047', 'SP-000040'), -- Omeprazole 20mg
('KM-20251101-0047', 'SP-000041'), -- Pantoprazole 40mg
('KM-20251101-0047', 'SP-000042'), -- Esomeprazole 40mg
('KM-20251101-0047', 'SP-000043'), -- Domperidone 10mg
('KM-20251101-0047', 'SP-000044'), -- Loperamide 2mg
('KM-20251101-0047', 'SP-000045'), -- Smecta
('KM-20251101-0047', 'SP-000046'), -- Phosphalugel
('KM-20251101-0047', 'SP-000047'), -- Gaviscon
('KM-20251101-0047', 'SP-000048'), -- Duphalac

-- KM-20260101-0048: Thuốc bổ não và tim mạch (12 SP)
('KM-20260101-0048', 'SP-000018'), -- Thuốc bổ gan Boganic
('KM-20260101-0048', 'SP-000019'), -- Thuốc bổ mắt Bilberry
('KM-20260101-0048', 'SP-000037'), -- Losartan 50mg
('KM-20260101-0048', 'SP-000038'), -- Amlodipine 5mg
('KM-20260101-0048', 'SP-000039'), -- Atorvastatin 20mg
('KM-20260101-0048', 'SP-000147'), -- Thuốc bổ não Ginkgo Biloba
('KM-20260101-0048', 'SP-000148'), -- Thuốc bổ thận
('KM-20260101-0048', 'SP-000161'), -- Thuốc tim mạch Bisoprolol
('KM-20260101-0048', 'SP-000162'), -- Thuốc tim mạch Amiodarone
('KM-20260101-0048', 'SP-000268'), -- Thuốc trị cao mỡ máu Rosuvastatin
('KM-20260101-0048', 'SP-000269'), -- Thuốc trị cao mỡ máu Fenofibrate
('KM-20260101-0048', 'SP-000274'), -- Thuốc trị suy giáp Levothyroxine

-- KM-20260101-0049: Sản phẩm chăm sóc da (13 SP)
('KM-20260101-0049', 'SP-000114'), -- Kem trị thâm mụn
('KM-20260101-0049', 'SP-000115'), -- Mặt nạ dưỡng da
('KM-20260101-0049', 'SP-000116'), -- Toner cân bằng da
('KM-20260101-0049', 'SP-000117'), -- Kem mắt chống lão hóa
('KM-20260101-0049', 'SP-000118'), -- Kem body dưỡng trắng
('KM-20260101-0049', 'SP-000119'), -- Dầu dưỡng tóc Moroccan
('KM-20260101-0049', 'SP-000120'), -- Xịt khoáng Avene
('KM-20260101-0049', 'SP-000244'), -- Serum retinol
('KM-20260101-0049', 'SP-000245'), -- Serum niacinamide
('KM-20260101-0049', 'SP-000246'), -- Serum hyaluronic acid
('KM-20260101-0049', 'SP-000247'), -- Kem trị nám
('KM-20260101-0049', 'SP-000248'), -- Kem trị tàn nhang
('KM-20260101-0049', 'SP-000249'), -- Gel rửa mặt trị mụn

-- KM-20260101-0050: Thuốc giảm đau và kháng viêm (11 SP)
('KM-20260101-0050', 'SP-000022'), -- Ibuprofen 200mg
('KM-20260101-0050', 'SP-000025'), -- Paracetamol 500mg
('KM-20260101-0050', 'SP-000031'), -- Panadol Extra
('KM-20260101-0050', 'SP-000053'), -- Prednisolone 5mg
('KM-20260101-0050', 'SP-000054'), -- Dexamethasone 0.5mg
('KM-20260101-0050', 'SP-000055'), -- Methylprednisolone 16mg
('KM-20260101-0050', 'SP-000056'), -- Diclofenac 50mg
('KM-20260101-0050', 'SP-000057'), -- Meloxicam 7.5mg
('KM-20260101-0050', 'SP-000058'), -- Celecoxib 200mg
('KM-20260101-0050', 'SP-000059'), -- Tramadol 50mg
('KM-20260101-0050', 'SP-000126'); -- Miếng dán giảm đau Salonpas
GO
