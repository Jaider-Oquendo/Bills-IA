package com.example.billsia.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.billsia.EducativoFragment
import com.example.billsia.R

class EducativoContainerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_educativo_container, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Mostrar EducativoFragment como fragmento inicial
        childFragmentManager.beginTransaction()
            .replace(R.id.innerFragmentContainer, EducativoFragment())
            .commit()
    }
}
