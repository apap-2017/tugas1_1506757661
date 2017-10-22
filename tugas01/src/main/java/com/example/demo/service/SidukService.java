package com.example.demo.service;

import java.util.List;

import com.example.demo.model.KecamatanModel;
import com.example.demo.model.KeluargaModel;
import com.example.demo.model.KelurahanModel;
import com.example.demo.model.KotaModel;
import com.example.demo.model.PendudukModel;

public interface SidukService {
	
	PendudukModel selectPenduduk(String nik);
	KeluargaModel selectKeluarga(String nkk);
	List<PendudukModel> selectAnggotaKeluarga(String nkk);
	KelurahanModel selectKelurahanByID(long id);
	KecamatanModel selectKecamatanByID(long id);
	KotaModel selectKotaByID(long id);
	void addPenduduk(PendudukModel penduduk);
	String selectKodeKelurahan(String id_kelurahan);
	int countPendudukRow();
	void addKeluarga(KeluargaModel keluarga);
	String selectIDKelurahan(String nama_kelurahan);
	int countKeluargaRow();
	void updatePenduduk (PendudukModel penduduk);
	void updateKeluarga (KeluargaModel keluarga);
	void nonAktifkan (String nik);
	void nonAktifkanKeluarga (String nkk);
	String selectKeluargaNKKByID(long id);
	List<KotaModel> selectAllKota();
	List<KecamatanModel> selectKecamatanByIDKota(long id);
	List<KelurahanModel> selectKelurahanByIDKecamatan(long id);
    List<PendudukModel> selectPendudukByDomisili(long idKota, long idKecamatan, long idKelurahan);
	
    

}
