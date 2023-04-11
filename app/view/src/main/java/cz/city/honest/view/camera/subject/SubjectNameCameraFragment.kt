package cz.city.honest.view.camera.subject

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import cz.city.honest.dto.WatchedSubject
import cz.city.honest.view.R
import cz.city.honest.view.camera.CameraFragment
import cz.city.honest.view.camera.analyzer.SubjectNameAnalyzer
import cz.city.honest.view.detail.SubjectDetailActivity
import cz.city.honest.viewmodel.CameraResultViewModel

class SubjectNameCameraFragment : CameraFragment() {

    private lateinit var cameraResultViewModel: CameraResultViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_subject_name_camera, container, false)
        initViewModels()
        cameraViewModel.nameResult.observe(viewLifecycleOwner) { handleCameraResult(it) }
        return root
    }

    private fun handleCameraResult(image: String){
        cameraResultViewModel.suggestNewSubject(image)
            .also { redirectToSuggestionDetail(it) }
    }

    private fun redirectToSuggestionDetail(watchedSubject: WatchedSubject) =
        Intent(activity, SubjectDetailActivity::class.java)
            .also {  it.putExtra(SubjectDetailActivity.WATCHED_SUBJECT, watchedSubject) }
            .also { startActivity(it) }

    private fun initViewModels() {
        setViewModels()
        cameraResultViewModel = ViewModelProvider(this, viewModelFactory).get(CameraResultViewModel::class.java)
    }

    override fun getImageAnalyzer() =
        super.getImageAnalyzer()
            .also {
                it.setAnalyzer(
                    cameraExecutor,
                    SubjectNameAnalyzer(
                        cameraViewModel
                    )
                )
            }

}