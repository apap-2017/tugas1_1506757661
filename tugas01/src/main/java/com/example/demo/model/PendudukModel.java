package com.example.demo.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PendudukModel {
	private long id;
	private String nik;
	private String nik_lama;
	private String nama;
	private String tempat_lahir;
	private String tanggal_lahir;
	private String jenis_kelamin;
	private String is_wni;
	private long id_keluarga;
	private String agama;
	private String pekerjaan;
	private String status_perkawinan;
	private String status_dalam_keluarga;
	private String golongan_darah;
	private String is_wafat;
	private String alamat;
	private String rt;
	private String rw;
	private String kelurahan;
	private String kecamatan;
	private String kota;
	



	public PendudukModel changeDefault(String nik) {
		if (this.getIs_wni().equals("1")) {
			this.setIs_wni("WNI");
		} else {
			this.setIs_wni("WNA");
		}
		
		if (this.getJenis_kelamin().equals("1")) {
			this.setJenis_kelamin("Wanita");
		} else {
			this.setJenis_kelamin("Pria");        		
		}
		
		if (this.getIs_wafat().equals("0")) {
			this.setIs_wafat("Hidup");
		} else {
			this.setIs_wafat("Mati");        		
		}
		return this;
	}


}


