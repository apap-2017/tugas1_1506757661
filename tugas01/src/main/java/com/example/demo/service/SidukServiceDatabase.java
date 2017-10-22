package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.SidukMapper;
import com.example.demo.model.KecamatanModel;
import com.example.demo.model.KeluargaModel;
import com.example.demo.model.KelurahanModel;
import com.example.demo.model.KotaModel;
import com.example.demo.model.PendudukModel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SidukServiceDatabase implements SidukService{
	
	@Autowired
	private SidukMapper sidukMapper;
	
	@Override
	public PendudukModel selectPenduduk(String nik) {
		// TODO Auto-generated method stub
	     log.info ("select penduduk with nik {}", nik);
       return sidukMapper.selectPenduduk (nik);
  }

	@Override
	public KeluargaModel selectKeluarga(String nkk) {
	     log.info ("select keluarga with nkk {}", nkk);
	       return sidukMapper.selectKeluarga(nkk);
	}

	@Override
	public KelurahanModel selectKelurahanByID(long id) {
	     log.info ("select kelurahan with id {}", id);
	       return sidukMapper.selectKelurahanByID(id);
	}

	@Override
	public KecamatanModel selectKecamatanByID(long id) {
	     log.info ("select penduduk with nik {}", id);
	       return sidukMapper.selectKecamatanByID(id);
	}

	@Override
	public KotaModel selectKotaByID(long id) {
	     log.info ("select penduduk with nik {}", id);
	       return sidukMapper.selectKotaByID(id);
	}

	@Override
	public List<PendudukModel> selectAnggotaKeluarga(String nkk) {
		// TODO Auto-generated method stub
		 log.info ("select anggota keluarga with id_keluarga {}", nkk);
	       return sidukMapper.selectAnggotaKeluarga(nkk);
	}

	@Override
	public void addPenduduk(PendudukModel penduduk) {
		// TODO Auto-generated method stub
		 log.info ("add penduduk {}", penduduk);
	     sidukMapper.addPenduduk(penduduk);
	
	}

	@Override
	public String selectKodeKelurahan(String id_kelurahan) {
		// TODO Auto-generated method stub
		log.info ("select KodeKelurahan {}", id_kelurahan);
		return sidukMapper.selectKodeKelurahan(id_kelurahan);
	}

	@Override
	public int countPendudukRow() {
		log.info ("select countPendudukRow {}");
		return sidukMapper.countPendudukRow();
	}

	@Override
	public void addKeluarga(KeluargaModel keluarga) {
		 log.info ("add keluarga {}", keluarga);
	     sidukMapper.addKeluarga(keluarga);
		
	}

	@Override
	public String selectIDKelurahan(String nama_kelurahan) {
		log.info ("select IDKelurahan {}", nama_kelurahan);
		return sidukMapper.selectIDKelurahan(nama_kelurahan);
	}

	@Override
	public int countKeluargaRow() {
		log.info ("select countKeluargaRow {}");
		return sidukMapper.countKeluargaRow();
	}

	@Override
	public void updatePenduduk(PendudukModel penduduk) {
		log.info ("update penduduk {}");
		sidukMapper.updatePenduduk(penduduk);
		
	}

	@Override
	public void updateKeluarga(KeluargaModel keluarga) {
		// TODO Auto-generated method stub
		log.info ("update keluarga {}");
		sidukMapper.updateKeluarga(keluarga);
	}

	@Override
	public void nonAktifkan(String nik) {
		// TODO Auto-generated method stub
		log.info ("nonaktifkan {}");
		sidukMapper.nonAktifkan(nik);
	}
	
	@Override
	public void nonAktifkanKeluarga(String nkk) {
		// TODO Auto-generated method stub
		log.info ("nonaktifkan {}");
		sidukMapper.nonAktifkanKeluarga(nkk);
	}

	@Override
	public String selectKeluargaNKKByID(long id) {
		log.info("select keluarga nkk by id {}");
		return sidukMapper.selectKeluargaNKKByID(id);
	}

	@Override
	public List<KotaModel> selectAllKota() {
		log.info("select kota  {}");
		return sidukMapper.selectAllKota();
	}

	@Override
	public List<KecamatanModel> selectKecamatanByIDKota(long id) {
		log.info("select kecamatan by idkota  {}");
		return sidukMapper.selectKecamatanByIDKota(id);
	}

	@Override
	public List<KelurahanModel> selectKelurahanByIDKecamatan(long id) {
		log.info("select kelurahan by idkecamatan {}");
		return sidukMapper.selectKelurahanByIDKecamatan(id);
	}

	@Override
	public List<PendudukModel> selectPendudukByDomisili(long idKota, long idKecamatan, long idKelurahan) {
		log.info("select penduduk by domisili  {}");
		return sidukMapper.selectPendudukByDomisili(idKota, idKecamatan, idKelurahan);
	}
	

}