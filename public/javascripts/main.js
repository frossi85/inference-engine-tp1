$(function() {
    $.postJSON = function(url, data, callback) {
      return $.ajax({
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        'type': 'POST',
        'url': url,
        'data': JSON.stringify(data),
        'dataType': 'json'
      }).done(callback)
      .fail(function(error) {
        console.log(error);
      });
    };

    function SelectViewModel(name, value) {
        this.name = name;
        this.value = value;
    };

    function MyViewModel() {
      var self = this;

      self.showWelcome = ko.observable(true);
      self.showDiagnosticForm = ko.observable(false);
      self.showDiagnosis = ko.observable(false);
      self.goToWelcome = function() {
        self.showWelcome(true);
        self.showDiagnosticForm(false);
        self.showDiagnosis(false);
      };

      self.newDiagnostic = function() {
        self.showWelcome(false);
        self.showDiagnosticForm(true);
        self.showDiagnosis(false);
      };

      self.getDiagnosis = function() {
        var kindOfPains = [];

        for(var i = 0; i < self.kindOfPains().length; i++) {
          kindOfPains.push({ name: self.kindOfPains()[i] });
        }

        var formData = {
          coloration: { name: self.selectedColoration().value },
          kindOfPains: kindOfPains,
          coldStimulus: { name: self.coldStimulus() },
          heatStimulus: { name: self.heatStimulus() },
          electricalStimulation: { name: self.electricalStimulation() },
          percussionStimulation: { name: self.percussionStimulation() },
          pulpState: { name: self.selectedPulpState().value },
          patientAge: { name: self.patientAge() },
          expectedDiagnosis: self.expectedDiagnosis().trim()
        };

        $.postJSON("http://localhost:9000", formData, function(diagnosis) {
          self.diagnosisResult(diagnosis.details);
          self.isHumanDiagnosisCorrect(diagnosis.isHumanDiagnosisCorrect);
        });

        self.showWelcome(false);
        self.showDiagnosticForm(false);
        self.showDiagnosis(true);
      };
      self.diagnosisResult = ko.observable("");

      self.colorationValues = ko.observableArray([
        new SelectViewModel("Normal", "normal"),
        new SelectViewModel("Grisáceo", "gray"),
        new SelectViewModel("Cafe", "brown"),
        new SelectViewModel("Amarillenta", "yellowish"),
        new SelectViewModel("Rojizo", "reddish")
      ]);
      self.selectedColoration = ko.observable(); // Nothing selected by default

      self.coldStimulus = ko.observable("negative");
      self.heatStimulus = ko.observable("negative");
      self.electricalStimulation = ko.observable("negative");
      self.percussionStimulation = ko.observable("negative");

      self.pulpStateValues = ko.observableArray([
        new SelectViewModel("Destruida", "shattered"),
        new SelectViewModel("Hipertrofiada", "hypertrophied"),
        new SelectViewModel("Atrofiada", "atrophied"),
        new SelectViewModel("Parcialmente destruída", "partiallyDestroyed"),
        new SelectViewModel("Íntegra", "intact")
      ]);
      self.selectedPulpState = ko.observable(); // Nothing selected by default


      self.kindOfPains = ko.observableArray([]);
      self.patientAge = ko.observable("young");

      self.expectedDiagnosis = ko.observable("");
      self.isHumanDiagnosisCorrect = ko.observable(false);

      self.hasExpectedDiagnosis = ko.computed(function() {
        return this.expectedDiagnosis().trim() != '';
      }, this);

    };

    ko.applyBindings(new MyViewModel());
});


// Freelancer Theme JavaScript
(function($) {
  "use strict"; // Start of use strict



  // Highlight the top nav as scrolling occurs
  $('body').scrollspy({
      target: '.navbar-fixed-top',
      offset: 51
  });

  // Closes the Responsive Menu on Menu Item Click
  $('.navbar-collapse ul li a:not(.dropdown-toggle)').click(function() {
      $('.navbar-toggle:visible').click();
  });

  // Offset for Main Navigation
  $('#mainNav').affix({
      offset: {
          top: 100
      }
  })

})(jQuery); // End of use strict