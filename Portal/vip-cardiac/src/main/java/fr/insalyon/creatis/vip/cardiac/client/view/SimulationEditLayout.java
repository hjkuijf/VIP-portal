/*
 * Copyright and authors: see LICENSE.txt in base repository.
 *
 * This software is a web portal for pipeline execution on distributed systems.
 *
 * This software is governed by the CeCILL-B license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
 * modify and/ or redistribute the software under the terms of the CeCILL-B
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info". 
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability. 
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or 
 * data to be ensured and,  more generally, to use and operate it in the 
 * same conditions as regards security. 
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-B license and that you accept its terms.
 */
package fr.insalyon.creatis.vip.cardiac.client.view;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.smartgwt.client.types.MultipleAppearance;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.RichTextEditor;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import fr.insalyon.creatis.vip.cardiac.client.CardiacConstants;
import fr.insalyon.creatis.vip.cardiac.client.bean.Simulation;
import fr.insalyon.creatis.vip.cardiac.client.rpc.CardiacService;
import fr.insalyon.creatis.vip.core.client.view.CoreConstants;
import fr.insalyon.creatis.vip.core.client.view.common.AbstractFormLayout;
import fr.insalyon.creatis.vip.core.client.view.layout.Layout;
import fr.insalyon.creatis.vip.core.client.view.util.FieldUtil;
import fr.insalyon.creatis.vip.core.client.view.util.WidgetUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author glatard
 */
public class SimulationEditLayout extends AbstractFormLayout {

    private boolean newSimulation = true;
    private TextItem nameField, simulationIDField;
    private TextArea filesField;
    private SelectItem modalitiesPickList;
    private RichTextEditor richTextEditor;
    private IButton saveButton;

    public SimulationEditLayout() {
        super(480, 200);
        addTitle("Add/Edit Cardiac Simulation", CardiacConstants.ICON_SD);
        configure();

    }

    private void configure() {
        nameField = FieldUtil.getTextItem(450, null);
        simulationIDField = FieldUtil.getTextItem(450, null);
        filesField = new TextArea();
        filesField.setTitle("Files");
        filesField.setWidth("450");
        filesField.setHeight("100");

        modalitiesPickList = new SelectItem();
        modalitiesPickList.setShowTitle(false);
        modalitiesPickList.setMultiple(true);
        modalitiesPickList.setMultipleAppearance(MultipleAppearance.PICKLIST);
        modalitiesPickList.setWidth(450);
        String[] modalities = {"CT", "MRI", "PET", "Ultrasound"};
        modalitiesPickList.setValueMap(modalities);

        richTextEditor = new RichTextEditor();
        richTextEditor.setHeight(400);
        richTextEditor.setOverflow(Overflow.HIDDEN);
        richTextEditor.setShowEdges(true);
        richTextEditor.setControlGroups("styleControls", "editControls",
                "colorControls", "insertControls");

        saveButton = WidgetUtil.getIButton("Save", CoreConstants.ICON_SAVED,
                new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        if (nameField.validate() && modalitiesPickList.validate()) {
                            List<String> values = new ArrayList<String>();
                            values.addAll(Arrays.asList(modalitiesPickList.getValues()));
                            String m = "";
                            for (String s : values) {
                                m += s + " ";
                            }
                            String id = "";
                            if (simulationIDField.getValue() != null) {
                                id = simulationIDField.getValueAsString().trim();
                            }
                            Simulation s = new Simulation(nameField.getValueAsString().trim(),
                                    richTextEditor.getValue(), filesField.getValue().trim(), m, id);
                            save(s);
                        }
                    }
                });
        addField("Name", nameField);
        addField("Modalities", modalitiesPickList);
        addField("VIP simulation ID", simulationIDField);
        this.addMember(WidgetUtil.getLabel("<b>Description</b>", 15));
        this.addMember(richTextEditor);

        this.addMember(WidgetUtil.getLabel("<b>Files</b> (LFNs, space-separated)", 15));
        this.addMember(filesField);
        addButtons(saveButton);
    }

    private void loadClasses() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void save(Simulation simulation) {
        WidgetUtil.setLoadingIButton(saveButton, "Saving...");

        if (newSimulation) {
            try {
                CardiacService.Util.getInstance().addSimulation(simulation, getCallback("add"));
            } catch (CardiacException ex) {
                Layout.getInstance().setWarningMessage("Unable to add simulation:<br />" + ex.getMessage());
            }
        } else {
            try {
                CardiacService.Util.getInstance().updateSimulation(simulation, getCallback("update"));
            } catch (CardiacException ex) {
                Layout.getInstance().setWarningMessage("Unable to update simulation:<br />" + ex.getMessage());
            }
        }

    }

    private AsyncCallback<Void> getCallback(final String text) {

        return new AsyncCallback<Void>() {

            @Override
            public void onFailure(Throwable caught) {
                WidgetUtil.resetIButton(saveButton, "Save", CoreConstants.ICON_SAVED);
                Layout.getInstance().setWarningMessage("Unable to " + text + " simulation:<br />" + caught.getMessage());
            }

            @Override
            public void onSuccess(Void result) {
                WidgetUtil.resetIButton(saveButton, "Save", CoreConstants.ICON_SAVED);
                //setApplication(null, null, null);
                CardiacTab tab = (CardiacTab) Layout.getInstance().
                        getTab(CardiacConstants.TAB_SD);
                tab.loadData();
            }
        };
    }

    void setSimulation(Simulation s) {
        if (s == null) {
            newSimulation = true;
            nameField.clearValue();
            filesField.setValue("");
            richTextEditor.setValue("");
            modalitiesPickList.clearValue();
            simulationIDField.clearValue();
        } else {
            newSimulation = false;
            nameField.setValue(s.getName());
            filesField.setValue(s.getFilesAsString());
            richTextEditor.setValue(s.getDescription());
            modalitiesPickList.setValues(s.getModalities().split(" "));
            simulationIDField.setValue(s.getSimulationID());
        }
    }
}
