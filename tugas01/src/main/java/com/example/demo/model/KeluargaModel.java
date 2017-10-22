package com.example.demo.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeluargaModel {
	private long id;
	private String nomor_kk;
	private String nomor_kk_lama;
	private String alamat;
	private String rt;
	private String rw;
	private String id_kelurahan;
	private String is_tidak_berlaku;
	private List<PendudukModel> anggotaKeluarga;
	private String kelurahan;
	private String kecamatan;
	private String kota;
	

}
