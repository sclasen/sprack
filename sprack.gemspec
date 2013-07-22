# coding: utf-8
require 'rake'

Gem::Specification.new do |spec|
  spec.name          = "sprack"
  spec.version       = "0.0.9"
  spec.authors       = ["Scott Clasen"]
  spec.email         = ["scott@heroku.com"]
  spec.description   = %q{Spray based Rack Handler for JRuby Apps}
  spec.summary       = %q{Spray based Rack Handler for JRuby Apps}
  spec.homepage      = ""
  spec.license       = "MIT"

  spec.files         = FileList['bin/*', 'target/scala-2.10/sprack.jar']
  spec.executables   = spec.files.grep(%r{^bin/}) { |f| File.basename(f) }
  spec.test_files    = spec.files.grep(%r{^(test|spec|features)/})

  spec.add_development_dependency "bundler", "~> 1.3"
  spec.add_development_dependency "rake"
end
