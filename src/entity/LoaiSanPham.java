package entity;

import java.util.Objects;

public class LoaiSanPham {

    private String maLoaiSanPham;
    private String tenLoaiSanPham;
    private String moTa;

    public LoaiSanPham() {
    }

    public LoaiSanPham(String maLoaiSanPham, String tenLoaiSanPham, String moTa) {
        setMaLoaiSanPham(maLoaiSanPham);
        setTenLoaiSanPham(tenLoaiSanPham);
        setMoTa(moTa);
    }

    public LoaiSanPham(String maLoaiSanPham) {
		this.maLoaiSanPham = maLoaiSanPham;
	}

	public LoaiSanPham(LoaiSanPham lsp) {
        this.maLoaiSanPham = lsp.maLoaiSanPham;
        this.tenLoaiSanPham = lsp.tenLoaiSanPham;
        this.moTa = lsp.moTa;
    }

    public String getMaLoaiSanPham() {
        return maLoaiSanPham;
    }

    public void setMaLoaiSanPham(String maLoaiSanPham) {
        if (maLoaiSanPham != null && maLoaiSanPham.matches("^LSP\\d{3}$")) {
            this.maLoaiSanPham = maLoaiSanPham;
        } else {
            throw new IllegalArgumentException("Mã loại sản phẩm không hợp lệ. Định dạng yêu cầu: LSPxxx");
        }
    }

    public String getTenLoaiSanPham() {
        return tenLoaiSanPham;
    }

    public void setTenLoaiSanPham(String tenLoaiSanPham) {
        if (tenLoaiSanPham == null || tenLoaiSanPham.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên loại sản phẩm không được rỗng.");
        }
        if (tenLoaiSanPham.length() > 50) {
            throw new IllegalArgumentException("Tên loại sản phẩm không được vượt quá 50 ký tự.");
        }
        this.tenLoaiSanPham = tenLoaiSanPham;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        if (moTa != null && moTa.length() > 200) {
            throw new IllegalArgumentException("Mô tả quá dài, không được vượt quá 200 ký tự.");
        }
        this.moTa = moTa;
    }

    @Override
    public String toString() {
        return "LoaiSanPham{" +
                "maLoaiSanPham='" + maLoaiSanPham + '\'' +
                ", tenLoaiSanPham='" + tenLoaiSanPham + '\'' +
                ", moTa='" + moTa + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoaiSanPham that = (LoaiSanPham) o;
        return Objects.equals(maLoaiSanPham, that.maLoaiSanPham);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maLoaiSanPham);
    }
}