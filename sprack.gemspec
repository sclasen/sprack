# coding: utf-8
lib = File.expand_path('../lib', __FILE__)
$LOAD_PATH.unshift(lib) unless $LOAD_PATH.include?(lib)
require 'sprack/version'
require 'rake'

Gem::Specification.new do |spec|
  spec.name          = "sprack"
  spec.version       = Sprack::VERSION
  spec.authors       = ["Scott Clasen"]
  spec.email         = ["scott@heroku.com"]
  spec.description   = %q{Spray based Rack Handler for JRuby Apps}
  spec.summary       = %q{Spray based Rack Handler for JRuby Apps}
  spec.homepage      = ""
  spec.license       = "MIT"

  spec.files         = FileList['lib/**/*.rb', 'bin/*', 'target/scala-2.10/sprack.jar'] 
  spec.executables   = spec.files.grep(%r{^bin/}) { |f| File.basename(f) }
  spec.test_files    = spec.files.grep(%r{^(test|spec|features)/})
  spec.require_paths = ["lib"]

  spec.add_development_dependency "bundler", "~> 1.3"
  spec.add_development_dependency "rake"
end
